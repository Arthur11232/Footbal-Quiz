package com.arthuralexandryan.footballquiz.domain.progression

class ProgressionService(private val dataSource: ProgressionDataSource) {
    fun getSnapshot(): ProgressionSnapshot = buildSnapshot(nowMillis = null)

    fun refresh(nowMillis: Long): ProgressionSnapshot = buildSnapshot(nowMillis)

    fun getCompletedStageRecords(): List<CompletedStageRecord> = dataSource.getCompletedStageRecords()

    fun replaceCompletedStageRecords(records: List<CompletedStageRecord>) {
        dataSource.replaceCompletedStageRecords(records)
    }

    private fun buildSnapshot(nowMillis: Long?): ProgressionSnapshot {
        val snapshot = ProgressionRules.evaluate(
            progress = dataSource.getStageProgress(),
            completedStages = dataSource.getCompletedStageRecords(),
            nowMillis = nowMillis
        )
        snapshot.newlyCompleted.forEach(dataSource::saveCompletedStage)
        return snapshot
    }
}
