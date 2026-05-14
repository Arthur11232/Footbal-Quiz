package com.arthuralexandryan.footballquiz.presentation.start

import androidx.annotation.StringRes
import com.arthuralexandryan.footballquiz.models.UserStatsDTO

sealed class StartPageEffect {
    data class NavigateToChoose(val startNewGame: Boolean) : StartPageEffect()
    data object ShowNewGameDialog : StartPageEffect()
    data object OpenLanguageDialog : StartPageEffect()
    data object OpenProfile : StartPageEffect()
    data class PromptCloudRestore(
        val userId: String,
        val cloudStats: UserStatsDTO
    ) : StartPageEffect()
    data class ShowToastRes(
        @StringRes val messageResId: Int
    ) : StartPageEffect()
}
