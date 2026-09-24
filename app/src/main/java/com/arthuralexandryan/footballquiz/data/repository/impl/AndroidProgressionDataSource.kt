package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.GameProgressRepository
import com.arthuralexandryan.footballquiz.domain.progression.CompletedStageRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionDataSource
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.progression.StageProgress
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer

internal class AndroidProgressionDataSource(
    private val context: Context,
    private val gameProgressRepository: GameProgressRepository
) : ProgressionDataSource {

    override fun getStageProgress(): List<StageProgress> =
        gameProgressRepository.getProgressSnapshot().toStageProgress()

    override fun getCompletedStageRecords(): List<CompletedStageRecord> =
        ProgressionStageId.entries.mapNotNull { stageId ->
            val achievedAt = Prefer.getLongPreference(
                context,
                Constants.ProgressionStageAchievedAtKeyPrefix + stageId.value,
                0L
            ).takeIf { it > 0L } ?: return@mapNotNull null
            CompletedStageRecord(stageId, achievedAt)
        }

    override fun saveCompletedStage(record: CompletedStageRecord) {
        Prefer.setLongPreference(
            context,
            Constants.ProgressionStageAchievedAtKeyPrefix + record.stageId.value,
            record.achievedAtMillis
        )
    }

    override fun replaceCompletedStageRecords(records: List<CompletedStageRecord>) {
        val editor = Prefer.getSharedPreferenceEditor(context)
        ProgressionStageId.entries.forEach { stageId ->
            editor.remove(Constants.ProgressionStageAchievedAtKeyPrefix + stageId.value)
        }
        records.forEach { record ->
            editor.putLong(
                Constants.ProgressionStageAchievedAtKeyPrefix + record.stageId.value,
                record.achievedAtMillis
            )
        }
        editor.apply()
    }
}
