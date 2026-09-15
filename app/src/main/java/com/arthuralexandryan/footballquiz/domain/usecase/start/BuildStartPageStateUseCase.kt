package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import com.arthuralexandryan.footballquiz.presentation.start.StartPageUiState

class BuildStartPageStateUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(user: AuthenticatedUser?): StartPageUiState {
        val isNewGame = settingsRepository.isFirstPlay()
        return StartPageUiState(
            isContinueEnabled = !isNewGame,
            isNewGame = isNewGame,
            isSignedIn = user != null,
            accountEmail = user?.email
        )
    }
}
