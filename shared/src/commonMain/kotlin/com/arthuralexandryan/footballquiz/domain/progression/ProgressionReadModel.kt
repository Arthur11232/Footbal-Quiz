package com.arthuralexandryan.footballquiz.domain.progression

enum class StagePresentationState {
    IN_PROGRESS,
    COMPLETED_NOW,
    ACHIEVED
}

data class StageReadModel(
    val stageId: ProgressionStageId,
    val state: StagePresentationState,
    val achievedAtMillis: Long?
)

object ProgressionReadModelMapper {
    fun map(snapshot: ProgressionSnapshot): List<StageReadModel> = snapshot.stages.map { status ->
        val state = when {
            status.isAchieved -> StagePresentationState.ACHIEVED
            status.isCurrentlyCompleted -> StagePresentationState.COMPLETED_NOW
            else -> StagePresentationState.IN_PROGRESS
        }
        StageReadModel(status.stageId, state, status.achievedAtMillis)
    }
}

class DemoProgressReadModel {
    private val stages = ProgressionReadModelMapper.map(
        ProgressionRules.evaluate(
            progress = listOf(
                StageProgress(ProgressionStageId.TOP5_FRANCE_COMPLETE, 40, 40),
                StageProgress(ProgressionStageId.TOP5_GERMANY_COMPLETE, 40, 40),
                StageProgress(ProgressionStageId.TOP5_ITALY_COMPLETE, 21, 40)
            ),
            completedStages = listOf(
                CompletedStageRecord(ProgressionStageId.TOP5_FRANCE_COMPLETE, 1_700_000_000_000L)
            )
        )
    )

    val stageCount: Int get() = stages.size

    fun stageIdAt(index: Int): String = stages[index].stageId.value

    fun stageStateAt(index: Int): String = stages[index].state.name
}
