package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.domain.progression.CompletedStageRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionSnapshot

interface ProgressionRepository {
    fun getProgressionSnapshot(): ProgressionSnapshot
    fun refreshProgression(nowMillis: Long = System.currentTimeMillis()): ProgressionSnapshot
    fun getCompletedStageRecords(): List<CompletedStageRecord>
    fun replaceCompletedStageRecords(records: List<CompletedStageRecord>)
}
