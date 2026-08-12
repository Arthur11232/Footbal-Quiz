package com.arthuralexandryan.footballquiz.presentation.start

import androidx.annotation.StringRes
import com.arthuralexandryan.footballquiz.domain.sync.CloudProgressSnapshot

sealed class StartPageEffect {
    data class NavigateToChoose(val startNewGame: Boolean) : StartPageEffect()
    data object ShowNewGameDialog : StartPageEffect()
    data object OpenLanguageDialog : StartPageEffect()
    data object OpenProfile : StartPageEffect()
    data class PromptNotificationPermission(
        val userId: String
    ) : StartPageEffect()
    data class PromptCloudRestore(
        val userId: String,
        val cloudProgress: CloudProgressSnapshot
    ) : StartPageEffect()
    data class ShowToastRes(
        @StringRes val messageResId: Int
    ) : StartPageEffect()
}
