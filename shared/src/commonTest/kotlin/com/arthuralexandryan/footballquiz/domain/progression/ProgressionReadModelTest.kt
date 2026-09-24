package com.arthuralexandryan.footballquiz.domain.progression

import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressionReadModelTest {
    @Test
    fun keepsStageOrderAndDistinguishesAchievementsFromCurrentCompletion() {
        val snapshot = ProgressionRules.evaluate(
            progress = listOf(
                StageProgress(ProgressionStageId.TOP5_FRANCE_COMPLETE, 10, 40),
                StageProgress(ProgressionStageId.TOP5_GERMANY_COMPLETE, 40, 40)
            ),
            completedStages = listOf(
                CompletedStageRecord(ProgressionStageId.TOP5_FRANCE_COMPLETE, 123L)
            )
        )

        val models = ProgressionReadModelMapper.map(snapshot)
        assertEquals(ProgressionStageId.entries.toList(), models.map { it.stageId })
        assertEquals(StagePresentationState.ACHIEVED, models[0].state)
        assertEquals(123L, models[0].achievedAtMillis)
        assertEquals(StagePresentationState.COMPLETED_NOW, models[1].state)
        assertEquals(StagePresentationState.IN_PROGRESS, models[2].state)
    }

    @Test
    fun demoModelIsReadOnlyAndDoesNotClaimCertificates() {
        val demo = DemoProgressReadModel()
        assertEquals(16, demo.stageCount)
        assertEquals("top5_france_complete", demo.stageIdAt(0))
        assertEquals("ACHIEVED", demo.stageStateAt(0))
        assertEquals("COMPLETED_NOW", demo.stageStateAt(1))
        assertEquals("IN_PROGRESS", demo.stageStateAt(2))
    }
}
