package com.arthuralexandryan.footballquiz.presentation.start

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.arthuralexandryan.footballquiz.data.repository.QuestionSyncCompatibilityException
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.domain.usecase.notifications.MarkNotificationPermissionPromptShownUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.notifications.ShouldPromptNotificationPermissionUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.BuildStartPageStateUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.reward.ConsumePendingRewardsAfterAuthUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.EnsureLocalizationSyncUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.ResolveStartCloudSyncUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.RestoreStartCloudStatsUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.SignInWithGoogleTokenUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.UploadStartLocalStatsUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.notifications.SyncNotificationSessionUseCase
import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StartPageViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val buildStartPageStateUseCase: BuildStartPageStateUseCase,
    private val ensureLocalizationSyncUseCase: EnsureLocalizationSyncUseCase,
    private val signInWithGoogleTokenUseCase: SignInWithGoogleTokenUseCase,
    private val resolveStartCloudSyncUseCase: ResolveStartCloudSyncUseCase,
    private val uploadStartLocalStatsUseCase: UploadStartLocalStatsUseCase,
    private val restoreStartCloudStatsUseCase: RestoreStartCloudStatsUseCase,
    private val syncNotificationSessionUseCase: SyncNotificationSessionUseCase,
    private val shouldPromptNotificationPermissionUseCase: ShouldPromptNotificationPermissionUseCase,
    private val markNotificationPermissionPromptShownUseCase: MarkNotificationPermissionPromptShownUseCase,
    private val consumePendingRewardsAfterAuthUseCase: ConsumePendingRewardsAfterAuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StartPageUiState())
    val uiState: StateFlow<StartPageUiState> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<StartPageEffect>()
    val effects: SharedFlow<StartPageEffect> = _effects.asSharedFlow()

    fun refreshState() {
        applyState(authRepository.getCurrentUser())
    }

    fun showLoading(@StringRes messageResId: Int = R.string.loading) {
        _uiState.update {
            it.copy(
                isLoading = true,
                loadingMessageResId = messageResId
            )
        }
    }

    fun hideLoading() {
        _uiState.update {
            it.copy(
                isLoading = false,
                loadingMessageResId = R.string.loading
            )
        }
    }

    fun onGameClicked() {
        val isNewGame = _uiState.value.isNewGame
        showLoading()
        viewModelScope.launch {
            val result = ensureLocalizationSyncUseCase(isNewGame)
            hideLoading()
            result
                .onSuccess {
                    if (isNewGame) {
                        _effects.emit(StartPageEffect.NavigateToChoose(startNewGame = true))
                    } else {
                        _effects.emit(StartPageEffect.ShowNewGameDialog)
                    }
                }
                .onFailure {
                    val messageRes = if (it is QuestionSyncCompatibilityException) {
                        R.string.language_sync_not_ready
                    } else {
                        R.string.internet_connection_required
                    }
                    _effects.emit(StartPageEffect.ShowToastRes(messageRes))
                }
        }
    }

    fun onContinueClicked() {
        showLoading()
        viewModelScope.launch {
            val result = ensureLocalizationSyncUseCase(_uiState.value.isNewGame)
            hideLoading()
            result
                .onSuccess {
                    _effects.emit(StartPageEffect.NavigateToChoose(startNewGame = false))
                }
                .onFailure {
                    val messageRes = if (it is QuestionSyncCompatibilityException) {
                        R.string.language_sync_not_ready
                    } else {
                        R.string.internet_connection_required
                    }
                    _effects.emit(StartPageEffect.ShowToastRes(messageRes))
                }
        }
    }

    fun onLanguageClicked() {
        viewModelScope.launch {
            _effects.emit(StartPageEffect.OpenLanguageDialog)
        }
    }

    fun onProfileClicked() {
        viewModelScope.launch {
            _effects.emit(StartPageEffect.OpenProfile)
        }
    }

    fun onGoogleSignInIntentDataMissing() {
        hideLoading()
        applyState(null)
        emitToast(R.string.sign_in_failed)
    }

    fun onGoogleSignInIdTokenMissing() {
        hideLoading()
        applyState(null)
        emitToast(R.string.sign_in_failed)
    }

    fun onGoogleSignInFailure() {
        hideLoading()
        applyState(null)
        emitToast(R.string.sign_in_failed)
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        showLoading(R.string.signing_in)
        viewModelScope.launch {
            val authResult = signInWithGoogleTokenUseCase(idToken)
            hideLoading()
            if (authResult.success) {
                val user = authResult.user
                applyState(user)
                if (user != null) {
                    consumePendingRewardsAfterAuth(user)
                    syncNotificationSessionUseCase(user)
                    val cloudRestorePending = syncCloudProgressIfNeeded(user)
                    if (!cloudRestorePending) {
                        maybePromptNotificationPermission(user)
                    }
                }
            } else {
                applyState(null)
                emitToast(R.string.sign_in_failed)
            }
        }
    }

    fun onScreenStarted() {
        refreshState()
        val currentUser = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            consumePendingRewardsAfterAuth(currentUser)
            syncNotificationSessionUseCase(currentUser)
            val cloudRestorePending = syncCloudProgressIfNeeded(currentUser)
            if (!cloudRestorePending) {
                maybePromptNotificationPermission(currentUser)
            }
        }
    }

    fun onCloudRestoreConfirmed(userId: String, cloudStats: UserStatsDTO) {
        viewModelScope.launch {
            settingsRepository.setAutoRestorePromptDismissed(userId, false)
            restoreStartCloudStatsUseCase(userId, cloudStats)
            refreshState()
            emitToast(R.string.profile_progress_restored)
        }
    }

    fun onKeepLocalProgressConfirmed(userId: String) {
        settingsRepository.setAutoRestorePromptDismissed(userId, true)
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        val currentUser = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            syncNotificationSessionUseCase(currentUser)
            if (granted) {
                emitToast(R.string.notifications_permission_enabled)
            }
        }
    }

    private suspend fun syncCloudProgressIfNeeded(user: FirebaseUser): Boolean {
        val decision = resolveStartCloudSyncUseCase(user)
        if (
            decision is SyncRepository.SyncDecision.PromptConflict &&
            settingsRepository.isAutoRestorePromptDismissed(user.uid)
        ) {
            return false
        }

        when (decision) {
            SyncRepository.SyncDecision.None -> return false
            is SyncRepository.SyncDecision.AutoRestoreCloud -> {
                restoreStartCloudStatsUseCase(decision.userId, decision.cloudStats)
                refreshState()
                _effects.emit(StartPageEffect.ShowToastRes(R.string.profile_progress_restored))
                return false
            }
            is SyncRepository.SyncDecision.PromptConflict -> {
                _effects.emit(
                    StartPageEffect.PromptCloudRestore(
                        userId = decision.userId,
                        cloudStats = decision.cloudStats
                    )
                )
                return true
            }
            is SyncRepository.SyncDecision.UploadLocal -> {
                uploadStartLocalStatsUseCase(decision.user)
                    .onSuccess {
                        _effects.emit(StartPageEffect.ShowToastRes(R.string.profile_cloud_backup_created))
                    }
                return false
            }
        }
    }

    private suspend fun maybePromptNotificationPermission(user: FirebaseUser) {
        if (!shouldPromptNotificationPermissionUseCase(user.uid)) return
        markNotificationPermissionPromptShownUseCase(user.uid)
        _effects.emit(StartPageEffect.PromptNotificationPermission(user.uid))
    }

    private suspend fun consumePendingRewardsAfterAuth(user: FirebaseUser) {
        consumePendingRewardsAfterAuthUseCase(
            userId = user.uid,
            recipientName = resolveRecipientName(user),
            userEmail = user.email
        )
    }

    private fun resolveRecipientName(user: FirebaseUser): String {
        return user.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user.email
                ?.substringBefore("@")
                ?.takeIf { it.isNotBlank() }
            ?: "Player"
    }

    private fun applyState(user: FirebaseUser?) {
        val newState = buildStartPageStateUseCase(user)
        _uiState.update { currentState ->
            currentState.copy(
                isContinueEnabled = newState.isContinueEnabled,
                isNewGame = newState.isNewGame,
                isSignedIn = newState.isSignedIn,
                accountEmail = newState.accountEmail
            )
        }
    }

    private fun emitToast(@StringRes messageResId: Int) {
        viewModelScope.launch {
            _effects.emit(StartPageEffect.ShowToastRes(messageResId))
        }
    }
}
