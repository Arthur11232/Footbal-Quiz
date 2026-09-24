package com.arthuralexandryan.footballquiz.domain.progression

data class CompletedStageRecord(
    val stageId: ProgressionStageId,
    val achievedAtMillis: Long
)

data class ProgressionStageStatus(
    val stageId: ProgressionStageId,
    val isCurrentlyCompleted: Boolean,
    val isAchieved: Boolean,
    val achievedAtMillis: Long?
)

data class ProgressionSnapshot(
    val stages: List<ProgressionStageStatus>,
    val newlyCompleted: List<CompletedStageRecord>
)
