package com.arthuralexandryan.footballquiz.domain.progression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProgressionServiceTest {
    private val france = ProgressionStageId.TOP5_FRANCE_COMPLETE

    @Test
    fun readingDoesNotSaveAchievements() {
        val source = FakeProgressionDataSource(listOf(StageProgress(france, 40, 40)))
        val snapshot = ProgressionService(source).getSnapshot()
        assertTrue(snapshot.stages.single { it.stageId == france }.isCurrentlyCompleted)
        assertFalse(snapshot.stages.single { it.stageId == france }.isAchieved)
        assertTrue(source.completed.isEmpty())
    }

    @Test
    fun refreshSavesOnlyNewAchievementsOnce() {
        val source = FakeProgressionDataSource(listOf(StageProgress(france, 40, 40)))
        val service = ProgressionService(source)
        assertEquals(listOf(CompletedStageRecord(france, 100L)), service.refresh(100L).newlyCompleted)
        assertTrue(service.refresh(200L).newlyCompleted.isEmpty())
        assertEquals(listOf(CompletedStageRecord(france, 100L)), source.completed)
    }

    @Test
    fun restoringReplacesCompletedStages() {
        val source = FakeProgressionDataSource(listOf(StageProgress(france, 0, 40)))
        val service = ProgressionService(source)
        service.replaceCompletedStageRecords(listOf(CompletedStageRecord(france, 100L)))
        val status = service.getSnapshot().stages.single { it.stageId == france }
        assertTrue(status.isAchieved)
        assertFalse(status.isCurrentlyCompleted)
        assertEquals(100L, status.achievedAtMillis)
        service.replaceCompletedStageRecords(emptyList())
        assertTrue(service.getCompletedStageRecords().isEmpty())
    }

    private class FakeProgressionDataSource(
        private val progress: List<StageProgress>
    ) : ProgressionDataSource {
        val completed = mutableListOf<CompletedStageRecord>()

        override fun getStageProgress(): List<StageProgress> = progress

        override fun getCompletedStageRecords(): List<CompletedStageRecord> = completed.toList()

        override fun saveCompletedStage(record: CompletedStageRecord) {
            completed += record
        }

        override fun replaceCompletedStageRecords(records: List<CompletedStageRecord>) {
            completed.clear()
            completed.addAll(records)
        }
    }
}
