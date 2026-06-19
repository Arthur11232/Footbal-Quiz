package com.arthuralexandryan.footballquiz.presentation.achievement

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AchievementCertificatePayload(
    val primaryStageTitleResId: Int,
    val additionalStageTitleResIds: List<Int> = emptyList(),
    val recipientName: String,
    val issuedAtMillis: Long,
    val certificateId: String
) : Parcelable
