package com.arthuralexandryan.footballquiz.domain.reward

import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId

data class PendingRewardRecord(
    val stageId: ProgressionStageId,
    val certificateId: String,
    val earnedAtMillis: Long,
    val recipientNameSnapshot: String,
    val createdAsGuest: Boolean
)
