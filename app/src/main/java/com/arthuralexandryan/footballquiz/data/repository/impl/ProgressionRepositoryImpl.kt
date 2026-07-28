package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.GameProgressRepository
import com.arthuralexandryan.footballquiz.data.repository.ProfileProgressSnapshot
import com.arthuralexandryan.footballquiz.data.repository.ProgressionRepository
import com.arthuralexandryan.footballquiz.domain.progression.CompletedStageRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionSnapshot
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageStatus
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer

class ProgressionRepositoryImpl(
    private val context: Context,
    private val gameProgressRepository: GameProgressRepository
) : ProgressionRepository {

    override fun getProgressionSnapshot(): ProgressionSnapshot {
        return buildSnapshot(gameProgressRepository.getProgressSnapshot(), nowMillis = null, persistNew = false)
    }

    override fun refreshProgression(nowMillis: Long): ProgressionSnapshot {
        return buildSnapshot(gameProgressRepository.getProgressSnapshot(), nowMillis = nowMillis, persistNew = true)
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
        nowMillis: Long?,
        persistNew: Boolean
    ): ProgressionSnapshot {
        val newlyCompleted = mutableListOf<CompletedStageRecord>()
        val statuses = stageDefinitions.map { definition ->
            val isCurrentlyCompleted = definition.isCompleted(profile)
            val existingAchievedAt = getAchievedAt(definition.stageId)
            val achievedAt = when {
                existingAchievedAt != null -> existingAchievedAt
                isCurrentlyCompleted && persistNew && nowMillis != null -> {
                    persistAchievedAt(definition.stageId, nowMillis)
                    newlyCompleted += CompletedStageRecord(definition.stageId, nowMillis)
                    nowMillis
                }
                else -> null
            }

            ProgressionStageStatus(
                stageId = definition.stageId,
                isCurrentlyCompleted = isCurrentlyCompleted,
                isAchieved = achievedAt != null,
                achievedAtMillis = achievedAt
            )
        }

        return ProgressionSnapshot(
            stages = statuses,
            newlyCompleted = newlyCompleted
        )
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

    private data class StageDefinition(
        val stageId: ProgressionStageId,
        val isCompleted: (ProfileProgressSnapshot) -> Boolean
    )

    private companion object {
        val stageDefinitions = listOf(
            StageDefinition(ProgressionStageId.TOP5_FRANCE_COMPLETE) { it.franceAnswered >= it.franceTotal },
            StageDefinition(ProgressionStageId.TOP5_GERMANY_COMPLETE) { it.germanyAnswered >= it.germanyTotal },
            StageDefinition(ProgressionStageId.TOP5_ITALY_COMPLETE) { it.italyAnswered >= it.italyTotal },
            StageDefinition(ProgressionStageId.TOP5_ENGLAND_COMPLETE) { it.englandAnswered >= it.englandTotal },
            StageDefinition(ProgressionStageId.TOP5_SPAIN_COMPLETE) { it.spainAnswered >= it.spainTotal },
            StageDefinition(ProgressionStageId.TOP5_COMPLETE) { it.top5Answered >= it.top5Total },
            StageDefinition(ProgressionStageId.UEFA_SUPER_CUP_COMPLETE) { it.superCupAnswered >= it.superCupTotal },
            StageDefinition(ProgressionStageId.UEFA_EUROPA_LEAGUE_COMPLETE) { it.europaLeagueAnswered >= it.europaLeagueTotal },
            StageDefinition(ProgressionStageId.UEFA_CHAMPIONS_LEAGUE_COMPLETE) { it.championsLeagueAnswered >= it.championsLeagueTotal },
            StageDefinition(ProgressionStageId.UEFA_EUROPEAN_CHAMPIONSHIP_COMPLETE) {
                it.europeanChampionshipAnswered >= it.europeanChampionshipTotal
            },
            StageDefinition(ProgressionStageId.UEFA_COMPLETE) { it.uefaAnswered >= it.uefaTotal },
            StageDefinition(ProgressionStageId.WORLD_COMPLETE) { it.worldAnswered >= it.worldTotal },
            StageDefinition(ProgressionStageId.VERSUS_VSRM_COMPLETE) { it.vsrmAnswered >= it.vsrmTotal },
            StageDefinition(ProgressionStageId.VERSUS_VSRB_COMPLETE) { it.vsrbAnswered >= it.vsrbTotal },
            StageDefinition(ProgressionStageId.VERSUS_COMPLETE) { it.versusAnswered >= it.versusTotal },
            StageDefinition(ProgressionStageId.APP_COMPLETE) { it.totalAnswered >= it.totalQuestions }
        )
    }
}
