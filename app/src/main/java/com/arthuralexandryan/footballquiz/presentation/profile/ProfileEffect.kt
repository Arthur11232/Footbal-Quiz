package com.arthuralexandryan.footballquiz.presentation.profile

import androidx.annotation.StringRes
import com.arthuralexandryan.footballquiz.models.UserStatsDTO

sealed class ProfileEffect {
    data class ShowToastRes(
        @StringRes val messageResId: Int
    ) : ProfileEffect()

    data class ShowToastText(
        val message: String
    ) : ProfileEffect()

    data class ShowRestoreDialog(
        val cloudStats: UserStatsDTO
    ) : ProfileEffect()

    data class ShowOverwriteDialog(
        val localScore: Int,
        val localAnswered: Int
    ) : ProfileEffect()

    data object ShowDeleteAccountDialog : ProfileEffect()
    data object NavigateBackToStart : ProfileEffect()
}
