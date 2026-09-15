package com.arthuralexandryan.footballquiz.presentation.start

import androidx.annotation.StringRes
import com.arthuralexandryan.footballquiz.presentation.sync.CloudRestorePromptUiModel

sealed class StartPageEffect {
    data class NavigateToChoose(val startNewGame: Boolean) : StartPageEffect()
    data object ShowNewGameDialog : StartPageEffect()
    data object OpenLanguageDialog : StartPageEffect()
    data object OpenProfile : StartPageEffect()
    data class PromptNotificationPermission(
        val userId: String
    ) : StartPageEffect()
    data class PromptCloudRestore(
        val prompt: CloudRestorePromptUiModel
    ) : StartPageEffect()
    data class ShowToastRes(
        @param:StringRes val messageResId: Int
    ) : StartPageEffect()
}
