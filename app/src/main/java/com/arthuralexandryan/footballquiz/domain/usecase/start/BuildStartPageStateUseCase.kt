package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.presentation.start.StartPageUiState
import com.google.firebase.auth.FirebaseUser

class BuildStartPageStateUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(user: FirebaseUser?): StartPageUiState {
        val isNewGame = settingsRepository.isFirstPlay()
        return StartPageUiState(
            isContinueEnabled = !isNewGame,
            isNewGame = isNewGame,
            isSignedIn = user != null,
            accountEmail = user?.email
        )
    }
}
