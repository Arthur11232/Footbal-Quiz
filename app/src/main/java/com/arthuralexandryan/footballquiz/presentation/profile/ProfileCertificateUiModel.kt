package com.arthuralexandryan.footballquiz.presentation.profile

import androidx.annotation.StringRes
import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload

data class ProfileCertificateUiModel(
    @param:StringRes val stageTitleResId: Int,
    val issuedAtText: String,
    val certificateId: String,
    val recipientName: String,
    val previewPayload: AchievementCertificatePayload
)
