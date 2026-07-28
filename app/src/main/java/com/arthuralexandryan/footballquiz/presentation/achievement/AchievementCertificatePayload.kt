package com.arthuralexandryan.footballquiz.presentation.achievement

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AchievementCertificatePayload(
    val primaryStageId: String,
    val primaryStageTitleResId: Int,
    val additionalStageTitleResIds: List<Int> = emptyList(),
    val recipientName: String,
    val issuedAtMillis: Long,
    val certificateId: String,
    val isPendingAuthClaim: Boolean = false
) : Parcelable
