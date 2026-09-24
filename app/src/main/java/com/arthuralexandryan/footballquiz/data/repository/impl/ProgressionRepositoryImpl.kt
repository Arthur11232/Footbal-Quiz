package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.GameProgressRepository
import com.arthuralexandryan.footballquiz.data.repository.ProfileProgressSnapshot
import com.arthuralexandryan.footballquiz.data.repository.ProgressionRepository
import com.arthuralexandryan.footballquiz.domain.progression.CompletedStageRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionRules
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionSnapshot
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer

class ProgressionRepositoryImpl(
    private val context: Context,
    private val gameProgressRepository: GameProgressRepository
) : ProgressionRepository {

    override fun getProgressionSnapshot(): ProgressionSnapshot {
        return buildSnapshot(gameProgressRepository.getProgressSnapshot(), nowMillis = null)
    }

    override fun refreshProgression(nowMillis: Long): ProgressionSnapshot {
        return buildSnapshot(gameProgressRepository.getProgressSnapshot(), nowMillis = nowMillis)
    }

    override fun getCompletedStageRecords(): List<CompletedStageRecord> {
        return ProgressionStageId.entries.mapNotNull { stageId ->
            val achievedAt = getAchievedAt(stageId) ?: return@mapNotNull null
            CompletedStageRecord(stageId, achievedAt)
        }
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

    private fun buildSnapshot(
        profile: ProfileProgressSnapshot,
        nowMillis: Long?
    ): ProgressionSnapshot {
        val snapshot = ProgressionRules.evaluate(
            progress = profile.toStageProgress(),
            completedStages = getCompletedStageRecords(),
            nowMillis = nowMillis
        )
        snapshot.newlyCompleted.forEach { record ->
            persistAchievedAt(record.stageId, record.achievedAtMillis)
        }
        return snapshot
    }

    private fun getAchievedAt(stageId: ProgressionStageId): Long? {
        val value = Prefer.getLongPreference(
            context,
            Constants.ProgressionStageAchievedAtKeyPrefix + stageId.value,
            0L
        )
        return value.takeIf { it > 0L }
    }

    private fun persistAchievedAt(stageId: ProgressionStageId, achievedAt: Long) {
        Prefer.setLongPreference(
            context,
            Constants.ProgressionStageAchievedAtKeyPrefix + stageId.value,
            achievedAt
        )
    }
}
