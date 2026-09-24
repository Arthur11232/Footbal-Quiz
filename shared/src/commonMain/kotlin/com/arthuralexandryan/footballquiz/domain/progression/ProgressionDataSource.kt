package com.arthuralexandryan.footballquiz.domain.progression

interface ProgressionDataSource {
    fun getStageProgress(): List<StageProgress>
    fun getCompletedStageRecords(): List<CompletedStageRecord>
    fun saveCompletedStage(record: CompletedStageRecord)
    fun replaceCompletedStageRecords(records: List<CompletedStageRecord>)
}
