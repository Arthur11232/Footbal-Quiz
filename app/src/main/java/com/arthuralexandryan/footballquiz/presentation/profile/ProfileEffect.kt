package com.arthuralexandryan.footballquiz.presentation.profile

import androidx.annotation.StringRes
import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload
import com.arthuralexandryan.footballquiz.presentation.sync.CloudRestorePromptUiModel

sealed class ProfileEffect {
    data class ShowToastRes(
        @param:StringRes val messageResId: Int
    ) : ProfileEffect()

    data class ShowToastText(
        val message: String
    ) : ProfileEffect()

    data class ShowRestoreDialog(
        val prompt: CloudRestorePromptUiModel
    ) : ProfileEffect()

    data class ShowOverwriteDialog(
        val localScore: Int,
        val localAnswered: Int
    ) : ProfileEffect()

    data class OpenCertificatePreview(
        val payload: AchievementCertificatePayload
    ) : ProfileEffect()

    data object RequestNotificationPermission : ProfileEffect()
    data object ShowDeleteAccountDialog : ProfileEffect()
    data object NavigateBackToStart : ProfileEffect()
}
