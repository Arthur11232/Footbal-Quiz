package com.arthuralexandryan.footballquiz.domain.progression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProgressionRulesTest {
    private val thresholds = linkedMapOf(
        ProgressionStageId.TOP5_FRANCE_COMPLETE to 40,
        ProgressionStageId.TOP5_GERMANY_COMPLETE to 40,
        ProgressionStageId.TOP5_ITALY_COMPLETE to 40,
        ProgressionStageId.TOP5_ENGLAND_COMPLETE to 40,
        ProgressionStageId.TOP5_SPAIN_COMPLETE to 40,
        ProgressionStageId.TOP5_COMPLETE to 200,
        ProgressionStageId.UEFA_SUPER_CUP_COMPLETE to 20,
        ProgressionStageId.UEFA_EUROPA_LEAGUE_COMPLETE to 20,
        ProgressionStageId.UEFA_CHAMPIONS_LEAGUE_COMPLETE to 20,
        ProgressionStageId.UEFA_EUROPEAN_CHAMPIONSHIP_COMPLETE to 10,
        ProgressionStageId.UEFA_COMPLETE to 70,
        ProgressionStageId.WORLD_COMPLETE to 30,
        ProgressionStageId.VERSUS_VSRM_COMPLETE to 50,
        ProgressionStageId.VERSUS_VSRB_COMPLETE to 50,
        ProgressionStageId.VERSUS_COMPLETE to 100,
        ProgressionStageId.APP_COMPLETE to 400
    )

    @Test
    fun oneAnswerBelowEachThresholdDoesNotCompleteAnyStage() {
        val progress = thresholds.map { (stage, total) -> StageProgress(stage, total - 1, total) }
        val result = ProgressionRules.evaluate(progress, emptyList(), 100L)
        assertEquals(16, result.stages.size)
        assertTrue(result.stages.none { it.isCurrentlyCompleted || it.isAchieved })
        assertTrue(result.newlyCompleted.isEmpty())
    }

    @Test
    fun everyStageCompletesAtItsThresholdWithoutCompletingOtherStages() {
        thresholds.forEach { (stage, total) ->
            val progress = emptyProgress().map { if (it.stageId == stage) it.copy(answered = total) else it }
            val result = ProgressionRules.evaluate(progress, emptyList(), 100L)
            assertEquals(listOf(CompletedStageRecord(stage, 100L)), result.newlyCompleted)
            assertEquals(listOf(stage), result.stages.filter { it.isCurrentlyCompleted }.map { it.stageId })
        }
    }

    @Test
    fun aboveThresholdStillCompletesAllStagesInStableOrder() {
        val progress = thresholds.map { (stage, total) -> StageProgress(stage, total + 1, total) }.reversed()
        val result = ProgressionRules.evaluate(progress, emptyList(), 100L)
        assertEquals(thresholds.keys.toList(), result.stages.map { it.stageId })
        assertEquals(thresholds.keys.map { CompletedStageRecord(it, 100L) }, result.newlyCompleted)
    }

    @Test
    fun readingCompletedProgressDoesNotClaimAchievements() {
        val progress = thresholds.map { (stage, total) -> StageProgress(stage, total, total) }
        val result = ProgressionRules.evaluate(progress, emptyList())
        assertTrue(result.stages.all { it.isCurrentlyCompleted && !it.isAchieved })
        assertTrue(result.stages.all { it.achievedAtMillis == null })
        assertTrue(result.newlyCompleted.isEmpty())
    }

    @Test
    fun refreshAfterPersistingCompletionDoesNotIssueDuplicates() {
        val progress = thresholds.map { (stage, total) -> StageProgress(stage, total, total) }
        val first = ProgressionRules.evaluate(progress, emptyList(), 100L)
        val second = ProgressionRules.evaluate(progress, first.newlyCompleted, 200L)
        assertEquals(first.stages, second.stages)
        assertTrue(second.newlyCompleted.isEmpty())
    }

    @Test
    fun resetAndReplayRetainFirstAchievementDate() {
        val stage = ProgressionStageId.TOP5_FRANCE_COMPLETE
        val existing = listOf(CompletedStageRecord(stage, 100L))
        val reset = ProgressionRules.evaluate(emptyProgress(), existing, 200L)
        val status = reset.stages.single { it.stageId == stage }
        assertTrue(status.isAchieved)
        assertFalse(status.isCurrentlyCompleted)
        assertEquals(100L, status.achievedAtMillis)
        assertTrue(reset.newlyCompleted.isEmpty())
        val replay = ProgressionRules.evaluate(listOf(StageProgress(stage, 40, 40)), existing, 300L)
        assertTrue(replay.newlyCompleted.isEmpty())
        assertEquals(100L, replay.stages.single { it.stageId == stage }.achievedAtMillis)
    }

    @Test
    fun newStageDoesNotReplaceAlreadyAchievedStage() {
        val france = ProgressionStageId.TOP5_FRANCE_COMPLETE
        val germany = ProgressionStageId.TOP5_GERMANY_COMPLETE
        val progress = listOf(StageProgress(france, 40, 40), StageProgress(germany, 40, 40))
        val result = ProgressionRules.evaluate(progress, listOf(CompletedStageRecord(france, 100L)), 200L)
        assertEquals(listOf(CompletedStageRecord(germany, 200L)), result.newlyCompleted)
        assertEquals(100L, result.stages.single { it.stageId == france }.achievedAtMillis)
    }

    @Test
    fun zeroAnswersOrMissingProgressDoNotCreateAchievements() {
        listOf(emptyProgress(), emptyList()).forEach { progress ->
            val result = ProgressionRules.evaluate(progress, emptyList(), 100L)
            assertTrue(result.newlyCompleted.isEmpty())
            result.stages.forEach { assertNull(it.achievedAtMillis) }
        }
    }

    @Test
    fun explicitZeroThresholdPreservesLegacyComparison() {
        val stage = ProgressionStageId.WORLD_COMPLETE
        val result = ProgressionRules.evaluate(listOf(StageProgress(stage, 0, 0)), emptyList(), 100L)
        assertEquals(listOf(CompletedStageRecord(stage, 100L)), result.newlyCompleted)
    }

    private fun emptyProgress() = thresholds.map { (stage, total) -> StageProgress(stage, 0, total) }
}
