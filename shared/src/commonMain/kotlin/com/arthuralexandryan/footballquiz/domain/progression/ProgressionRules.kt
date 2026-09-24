package com.arthuralexandryan.footballquiz.domain.progression

object ProgressionRules {
    // A null timestamp reads current status without claiming new achievements.
    fun evaluate(
        progress: List<StageProgress>,
        completedStages: List<CompletedStageRecord>,
        nowMillis: Long? = null
    ): ProgressionSnapshot {
        val progressByStage = progress.associateBy { it.stageId }
        val completedByStage = completedStages.associateBy { it.stageId }
        val newlyCompleted = mutableListOf<CompletedStageRecord>()
        val statuses = ProgressionStageId.entries.map { stageId ->
            val stageProgress = progressByStage[stageId]
            val isCurrentlyCompleted = stageProgress != null &&
                stageProgress.answered >= stageProgress.total
            val existingAchievedAt = completedByStage[stageId]?.achievedAtMillis
            val achievedAt = when {
                existingAchievedAt != null -> existingAchievedAt
                isCurrentlyCompleted && nowMillis != null -> {
                    newlyCompleted += CompletedStageRecord(stageId, nowMillis)
                    nowMillis
                }
                else -> null
            }

            ProgressionStageStatus(
                stageId = stageId,
                isCurrentlyCompleted = isCurrentlyCompleted,
                isAchieved = achievedAt != null,
                achievedAtMillis = achievedAt
            )
        }
        return ProgressionSnapshot(stages = statuses, newlyCompleted = newlyCompleted)
    }
}
