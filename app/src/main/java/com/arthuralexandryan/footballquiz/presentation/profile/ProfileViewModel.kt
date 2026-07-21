package com.arthuralexandryan.footballquiz.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.arthuralexandryan.footballquiz.data.repository.CertificateRepository
import com.arthuralexandryan.footballquiz.data.repository.GameProgressRepository
import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository
import com.arthuralexandryan.footballquiz.data.repository.PendingRewardRepository
import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.data.repository.UserStatsRepository
import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.usecase.notifications.DeleteNotificationDevicesUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.notifications.SyncNotificationSessionUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.progression.RefreshProgressionUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.notifications.UnbindNotificationSessionUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.reward.ConsumePendingRewardsAfterAuthUseCase
import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload
import com.arthuralexandryan.footballquiz.presentation.progression.ProgressionUiModelMapper
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val gameProgressRepository: GameProgressRepository,
    private val syncRepository: SyncRepository,
    private val certificateRepository: CertificateRepository,
    private val userStatsRepository: UserStatsRepository,
    private val notificationRepository: NotificationRepository,
    private val pendingRewardRepository: PendingRewardRepository,
    private val refreshProgressionUseCase: RefreshProgressionUseCase,
    private val syncNotificationSessionUseCase: SyncNotificationSessionUseCase,
    private val unbindNotificationSessionUseCase: UnbindNotificationSessionUseCase,
    private val deleteNotificationDevicesUseCase: DeleteNotificationDevicesUseCase,
    private val consumePendingRewardsAfterAuthUseCase: ConsumePendingRewardsAfterAuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ProfileEffect>()
    val effects: SharedFlow<ProfileEffect> = _effects.asSharedFlow()

    fun loadProfile() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val certificateRecipientFallback = resolveRecipientName(user)

            if (user != null) {
                consumePendingRewardsAfterAuthUseCase(
                    userId = user.uid,
                    recipientName = certificateRecipientFallback,
                    userEmail = user.email
                )
            }

            val progressionSnapshot = refreshProgressionUseCase()
            val snapshot = gameProgressRepository.getProgressSnapshot()
            val completedStages = ProgressionUiModelMapper.buildCompletedStageUiModels(
                snapshot = progressionSnapshot,
                profile = snapshot
            )
            val missingCertificates = if (user != null) {
                progressionSnapshot.stages
                    .filter { it.isAchieved && it.achievedAtMillis != null }
                    .sortedBy { it.achievedAtMillis }
                    .mapNotNull { status ->
                        val achievedAtMillis = status.achievedAtMillis ?: return@mapNotNull null
                        if (certificateRepository.getIssuedCertificate(status.stageId) != null) return@mapNotNull null
                        IssuedCertificateRecord(
                            stageId = status.stageId,
                            certificateId = buildCertificateId(
                                stageKey = status.stageId.value,
                                issuedAtMillis = achievedAtMillis
                            ),
                            issuedAtMillis = achievedAtMillis,
                            recipientName = certificateRecipientFallback
                        )
                    }
            } else {
                emptyList()
            }
            if (missingCertificates.isNotEmpty()) {
                certificateRepository.saveIssuedCertificates(missingCertificates)
                if (user != null) {
                    certificateRepository.uploadIssuedCertificates(
                        userId = user.uid,
                        recipientName = certificateRecipientFallback,
                        userEmail = user.email,
                        records = missingCertificates
                    )
                }
            }

            val certificates = certificateRepository.getIssuedCertificates()
                .sortedByDescending { it.issuedAtMillis }
                .map { it.toProfileCertificateUiModel(certificateRecipientFallback) }
            val pendingRewardsCount = pendingRewardRepository.getPendingRewards().size
            val userId = user?.uid ?: "guest"
            val localPhotoPath = gameProgressRepository.getProfilePhotoPath(userId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    userName = user?.displayName ?: "",
                    userEmail = user?.email ?: "",
                    isSignedIn = user != null,
                    showNotificationSettings = user != null,
                    notificationsEnabled = notificationRepository.areNotificationsEnabled(),
                    progressNotificationsEnabled = notificationRepository.areProgressNotificationsEnabled(),
                    rewardNotificationsEnabled = notificationRepository.areRewardNotificationsEnabled(),
                    updateNotificationsEnabled = notificationRepository.areUpdateNotificationsEnabled(),
                    notificationPermissionGranted = notificationRepository.hasNotificationPermission(),
                    localPhotoPath = localPhotoPath,
                    remotePhotoUrl = user?.photoUrl?.toString(),
                    totalScoreText = "${snapshot.progressPercent}%",
                    answeredCountText = progressText(snapshot.totalAnswered, snapshot.totalQuestions),
                    top5ProgressText = progressText(snapshot.top5Answered, snapshot.top5Total),
                    uefaProgressText = progressText(snapshot.uefaAnswered, snapshot.uefaTotal),
                    worldProgressText = progressText(snapshot.worldAnswered, snapshot.worldTotal),
                    versusProgressText = progressText(snapshot.versusAnswered, snapshot.versusTotal),
                    certificates = certificates,
                    certificatesCount = certificates.size,
                    pendingRewardsCount = pendingRewardsCount,
                    completedStages = completedStages,
                    completedStagesCount = completedStages.size,
                    totalStagesCount = ProgressionStageId.entries.size
                )
            }
        }
    }

    private fun resolveRecipientName(user: FirebaseUser?): String {
        return user?.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user?.email
                ?.substringBefore("@")
                ?.takeIf { it.isNotBlank() }
            ?: "Guest"
    }

    fun onNotificationsEnabledChanged(enabled: Boolean) {
        notificationRepository.setNotificationsEnabled(enabled)
        updateNotificationState {
            it.copy(notificationsEnabled = enabled)
        }
        syncNotificationsAfterPreferenceChange(requestPermissionIfNeeded = enabled)
    }

    fun onProgressNotificationsEnabledChanged(enabled: Boolean) {
        notificationRepository.setProgressNotificationsEnabled(enabled)
        updateNotificationState {
            it.copy(progressNotificationsEnabled = enabled)
        }
        syncNotificationsAfterPreferenceChange()
    }

    fun onRewardNotificationsEnabledChanged(enabled: Boolean) {
        notificationRepository.setRewardNotificationsEnabled(enabled)
        updateNotificationState {
            it.copy(rewardNotificationsEnabled = enabled)
        }
        syncNotificationsAfterPreferenceChange()
    }

    fun onUpdateNotificationsEnabledChanged(enabled: Boolean) {
        notificationRepository.setUpdateNotificationsEnabled(enabled)
        updateNotificationState {
            it.copy(updateNotificationsEnabled = enabled)
        }
        syncNotificationsAfterPreferenceChange()
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        updateNotificationState {
            it.copy(notificationPermissionGranted = granted)
        }
        syncNotificationsAfterPreferenceChange(showSuccessToast = granted)
        if (!granted) {
            viewModelScope.launch {
                _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_notifications_permission_denied))
            }
        }
    }

    fun onCertificateClicked(certificate: ProfileCertificateUiModel) {
        viewModelScope.launch {
            _effects.emit(ProfileEffect.OpenCertificatePreview(certificate.previewPayload))
        }
    }

    fun onSignOutClicked() {
        val currentUserId = authRepository.getCurrentUser()?.uid
        viewModelScope.launch {
            setLoading(true)
            unbindNotificationSessionUseCase(currentUserId)
            authRepository.signOut()
            setLoading(false)
            loadProfile()
            _effects.emit(ProfileEffect.NavigateBackToStart)
        }
    }

    fun onDeleteAccountClicked() {
        viewModelScope.launch {
            _effects.emit(ProfileEffect.ShowDeleteAccountDialog)
        }
    }

    fun onDeleteAccountConfirmed() {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            setLoading(true)

            val cloudDevicesDeleted = deleteNotificationDevicesUseCase(user.uid)
            if (cloudDevicesDeleted.isFailure) {
                setLoading(false)
                _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_delete_error))
                return@launch
            }

            val cloudDeleted = userStatsRepository.deleteStats(user.uid)
            if (cloudDeleted.isFailure) {
                setLoading(false)
                _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_delete_error))
                return@launch
            }

            val cloudCertificatesDeleted = certificateRepository.deleteIssuedCertificates(user.uid)
            if (cloudCertificatesDeleted.isFailure) {
                setLoading(false)
                _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_delete_error))
                return@launch
            }

            notificationRepository.clearLocalSession()

            val localCleared = gameProgressRepository.clearLocalUserData(user.uid)
            if (localCleared.isFailure) {
                setLoading(false)
                _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_delete_error))
                return@launch
            }

            val deleteResult = authRepository.deleteUser(user)
            authRepository.signOut()

            setLoading(false)
            loadProfile()
            _effects.emit(
                ProfileEffect.ShowToastRes(
                    when {
                        deleteResult.isSuccess -> R.string.profile_delete_success
                        deleteResult.exceptionOrNull() is FirebaseAuthRecentLoginRequiredException -> {
                            R.string.profile_delete_reauth_required
                        }
                        else -> R.string.profile_delete_auth_error
                    }
                )
            )
            _effects.emit(ProfileEffect.NavigateBackToStart)
        }
    }

    fun onPhotoSelected(uri: Uri) {
        val userId = authRepository.getCurrentUser()?.uid ?: "guest"
        viewModelScope.launch {
            setLoading(true)
            val result = gameProgressRepository.saveProfilePhoto(userId, uri)
            setLoading(false)
            result
                .onSuccess {
                    loadProfile()
                    _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_edit_photo))
                }
                .onFailure {
                    _effects.emit(
                        ProfileEffect.ShowToastText(
                            "Failed to save image: ${it.message ?: "unknown error"}"
                        )
                    )
                }
        }
    }

    fun onRestoreCloudClicked() {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            setLoading(true)
            val cloudStats = userStatsRepository.downloadStats(user.uid)
            setLoading(false)
            if (cloudStats == null) {
                _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_no_cloud_progress))
            } else {
                _effects.emit(ProfileEffect.ShowRestoreDialog(cloudStats))
            }
        }
    }

    fun onRestoreCloudConfirmed(cloudStats: com.arthuralexandryan.footballquiz.models.UserStatsDTO) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            syncRepository.restoreCloudStats(userId, cloudStats)
            refreshProgressionUseCase()
            loadProfile()
            _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_progress_restored))
        }
    }

    fun onOverwriteCloudClicked() {
        val user = authRepository.getCurrentUser() ?: return
        val snapshot = gameProgressRepository.getProgressSnapshot()
        viewModelScope.launch {
            _effects.emit(
                ProfileEffect.ShowOverwriteDialog(
                    localScore = snapshot.totalScore,
                    localAnswered = snapshot.totalAnswered
                )
            )
        }
    }

    fun onOverwriteCloudConfirmed() {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            setLoading(true)
            val result = syncRepository.uploadLocalStats(user)
            setLoading(false)
            result
                .onSuccess {
                    gameProgressRepository.markAutoRestorePromptDismissed(user.uid)
                    _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_sync_success))
                }
                .onFailure {
                    _effects.emit(
                        ProfileEffect.ShowToastText(
                            "Sync failed: ${it.message ?: "Unknown error"}"
                        )
                    )
                }
        }
    }

    private fun progressText(answered: Int, total: Int): String = "$answered/$total"

    private fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    private fun updateNotificationState(update: (ProfileUiState) -> ProfileUiState) {
        _uiState.update(update)
    }

    private fun syncNotificationsAfterPreferenceChange(
        requestPermissionIfNeeded: Boolean = false,
        showSuccessToast: Boolean = false
    ) {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            if (requestPermissionIfNeeded && !notificationRepository.hasNotificationPermission()) {
                _effects.emit(ProfileEffect.RequestNotificationPermission)
            }

            val result = syncNotificationSessionUseCase(user)
            updateNotificationState {
                it.copy(notificationPermissionGranted = notificationRepository.hasNotificationPermission())
            }

            if (result.isFailure) {
                _effects.emit(ProfileEffect.ShowToastRes(R.string.profile_notifications_sync_failed))
                return@launch
            }

            if (showSuccessToast) {
                _effects.emit(ProfileEffect.ShowToastRes(R.string.notifications_permission_enabled))
            }
        }
    }

    private fun buildCertificateId(stageKey: String, issuedAtMillis: Long): String {
        val normalizedStageKey = stageKey
            .replace("_", "-")
            .uppercase()
            .take(18)
        val timeSuffix = issuedAtMillis.toString().takeLast(6)
        return "FQ-$normalizedStageKey-$timeSuffix"
    }

    private fun IssuedCertificateRecord.toProfileCertificateUiModel(
        fallbackRecipientName: String
    ): ProfileCertificateUiModel {
        val resolvedRecipientName = recipientName.ifBlank { fallbackRecipientName }
        return ProfileCertificateUiModel(
            stageTitleResId = ProgressionUiModelMapper.titleResIdFor(stageId),
            issuedAtText = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(issuedAtMillis)),
            certificateId = certificateId,
            recipientName = resolvedRecipientName,
            previewPayload = AchievementCertificatePayload(
                primaryStageId = stageId.value,
                primaryStageTitleResId = ProgressionUiModelMapper.titleResIdFor(stageId),
                additionalStageTitleResIds = emptyList(),
                recipientName = resolvedRecipientName,
                issuedAtMillis = issuedAtMillis,
                certificateId = certificateId,
                isPendingAuthClaim = false
            )
        )
    }
}
