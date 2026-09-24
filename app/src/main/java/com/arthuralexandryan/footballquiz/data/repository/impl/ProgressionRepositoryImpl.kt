package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.GameProgressRepository
import com.arthuralexandryan.footballquiz.data.repository.ProgressionRepository
import com.arthuralexandryan.footballquiz.domain.progression.CompletedStageRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionService
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionSnapshot

class ProgressionRepositoryImpl(
    context: Context,
    gameProgressRepository: GameProgressRepository
) : ProgressionRepository {
    private val service = ProgressionService(AndroidProgressionDataSource(context, gameProgressRepository))

    override fun getProgressionSnapshot(): ProgressionSnapshot = service.getSnapshot()

    override fun refreshProgression(nowMillis: Long): ProgressionSnapshot = service.refresh(nowMillis)

    override fun getCompletedStageRecords(): List<CompletedStageRecord> = service.getCompletedStageRecords()

    override fun replaceCompletedStageRecords(records: List<CompletedStageRecord>) {
        service.replaceCompletedStageRecords(records)
    }
}
