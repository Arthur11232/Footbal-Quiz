package com.arthuralexandryan.footballquiz.presentation.start

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.arthuralexandryan.footballquiz.data.repository.QuestionSyncCompatibilityException
import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.domain.usecase.start.BuildStartPageStateUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.EnsureLocalizationSyncUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.ResolveStartCloudSyncUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.RestoreStartCloudStatsUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.SignInWithGoogleTokenUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.UploadStartLocalStatsUseCase
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
    private val buildStartPageStateUseCase: BuildStartPageStateUseCase,
    private val ensureLocalizationSyncUseCase: EnsureLocalizationSyncUseCase,
    private val signInWithGoogleTokenUseCase: SignInWithGoogleTokenUseCase,
    private val resolveStartCloudSyncUseCase: ResolveStartCloudSyncUseCase,
    private val uploadStartLocalStatsUseCase: UploadStartLocalStatsUseCase,
    private val restoreStartCloudStatsUseCase: RestoreStartCloudStatsUseCase
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
                    syncCloudProgressIfNeeded(user)
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
            syncCloudProgressIfNeeded(currentUser)
        }
    }

    fun onCloudRestoreConfirmed(userId: String, cloudStats: UserStatsDTO) {
        restoreStartCloudStatsUseCase(userId, cloudStats)
        refreshState()
        emitToast(R.string.profile_progress_restored)
    }

    private suspend fun syncCloudProgressIfNeeded(user: FirebaseUser) {
        when (val decision = resolveStartCloudSyncUseCase(user)) {
            SyncRepository.SyncDecision.None -> Unit
            is SyncRepository.SyncDecision.RestoreCloud -> {
                _effects.emit(
                    StartPageEffect.PromptCloudRestore(
                        userId = decision.userId,
                        cloudStats = decision.cloudStats
                    )
                )
            }
            is SyncRepository.SyncDecision.UploadLocal -> {
                uploadStartLocalStatsUseCase(decision.user)
                    .onSuccess {
                        _effects.emit(StartPageEffect.ShowToastRes(R.string.profile_cloud_backup_created))
                    }
            }
        }
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
