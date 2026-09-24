package com.arthuralexandryan.footballquiz.presentation.progression

import androidx.annotation.StringRes
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.data.repository.ProfileProgressSnapshot
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionSnapshot
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionReadModelMapper
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.progression.StagePresentationState

data class CompletedStageUiModel(
    @param:StringRes val titleResId: Int,
    val progressText: String,
    val achievedAtMillis: Long
)

data class FreshlyCompletedStageUiModel(
    val stageId: ProgressionStageId,
    @param:StringRes val titleResId: Int,
    val achievedAtMillis: Long
)

object ProgressionUiModelMapper {

    fun buildCompletedStageUiModels(
        snapshot: ProgressionSnapshot,
        profile: ProfileProgressSnapshot
    ): List<CompletedStageUiModel> {
        return ProgressionReadModelMapper.map(snapshot)
            .filter { it.state == StagePresentationState.ACHIEVED && it.achievedAtMillis != null }
            .sortedByDescending { it.achievedAtMillis }
            .mapNotNull { status ->
                status.achievedAtMillis?.let { achievedAt ->
                    CompletedStageUiModel(
                        titleResId = titleResIdFor(status.stageId),
                        progressText = progressText(status.stageId, profile),
                        achievedAtMillis = achievedAt
                    )
                }
            }
    }

    fun buildFreshlyCompletedStageUiModels(snapshot: ProgressionSnapshot): List<FreshlyCompletedStageUiModel> {
        return snapshot.newlyCompleted.map { record ->
            FreshlyCompletedStageUiModel(
                stageId = record.stageId,
                titleResId = titleResIdFor(record.stageId),
                achievedAtMillis = record.achievedAtMillis
            )
        }
    }

    fun buildFreshlyCompletedStageUiModel(
        stageId: ProgressionStageId,
        achievedAtMillis: Long
    ): FreshlyCompletedStageUiModel {
        return FreshlyCompletedStageUiModel(
            stageId = stageId,
            titleResId = titleResIdFor(stageId),
            achievedAtMillis = achievedAtMillis
        )
    }

    private fun progressText(stageId: ProgressionStageId, profile: ProfileProgressSnapshot): String {
        val (answered, total) = progressNumbers(stageId, profile)
        return "$answered/$total"
    }

    private fun progressNumbers(
        stageId: ProgressionStageId,
        profile: ProfileProgressSnapshot
    ): Pair<Int, Int> {
        return when (stageId) {
            ProgressionStageId.TOP5_FRANCE_COMPLETE -> profile.franceAnswered to profile.franceTotal
            ProgressionStageId.TOP5_GERMANY_COMPLETE -> profile.germanyAnswered to profile.germanyTotal
            ProgressionStageId.TOP5_ITALY_COMPLETE -> profile.italyAnswered to profile.italyTotal
            ProgressionStageId.TOP5_ENGLAND_COMPLETE -> profile.englandAnswered to profile.englandTotal
            ProgressionStageId.TOP5_SPAIN_COMPLETE -> profile.spainAnswered to profile.spainTotal
            ProgressionStageId.TOP5_COMPLETE -> profile.top5Answered to profile.top5Total
            ProgressionStageId.UEFA_SUPER_CUP_COMPLETE -> profile.superCupAnswered to profile.superCupTotal
            ProgressionStageId.UEFA_EUROPA_LEAGUE_COMPLETE -> profile.europaLeagueAnswered to profile.europaLeagueTotal
            ProgressionStageId.UEFA_CHAMPIONS_LEAGUE_COMPLETE -> {
                profile.championsLeagueAnswered to profile.championsLeagueTotal
            }
            ProgressionStageId.UEFA_EUROPEAN_CHAMPIONSHIP_COMPLETE -> {
                profile.europeanChampionshipAnswered to profile.europeanChampionshipTotal
            }
            ProgressionStageId.UEFA_COMPLETE -> profile.uefaAnswered to profile.uefaTotal
            ProgressionStageId.WORLD_COMPLETE -> profile.worldAnswered to profile.worldTotal
            ProgressionStageId.VERSUS_VSRM_COMPLETE -> profile.vsrmAnswered to profile.vsrmTotal
            ProgressionStageId.VERSUS_VSRB_COMPLETE -> profile.vsrbAnswered to profile.vsrbTotal
            ProgressionStageId.VERSUS_COMPLETE -> profile.versusAnswered to profile.versusTotal
            ProgressionStageId.APP_COMPLETE -> profile.totalAnswered to profile.totalQuestions
        }
    }

    @StringRes
    fun titleResIdFor(stageId: ProgressionStageId): Int {
        return when (stageId) {
            ProgressionStageId.TOP5_FRANCE_COMPLETE -> R.string.progression_stage_top5_france
            ProgressionStageId.TOP5_GERMANY_COMPLETE -> R.string.progression_stage_top5_germany
            ProgressionStageId.TOP5_ITALY_COMPLETE -> R.string.progression_stage_top5_italy
            ProgressionStageId.TOP5_ENGLAND_COMPLETE -> R.string.progression_stage_top5_england
            ProgressionStageId.TOP5_SPAIN_COMPLETE -> R.string.progression_stage_top5_spain
            ProgressionStageId.TOP5_COMPLETE -> R.string.progression_stage_top5_complete
            ProgressionStageId.UEFA_SUPER_CUP_COMPLETE -> R.string.progression_stage_uefa_super_cup
            ProgressionStageId.UEFA_EUROPA_LEAGUE_COMPLETE -> R.string.progression_stage_uefa_europa_league
            ProgressionStageId.UEFA_CHAMPIONS_LEAGUE_COMPLETE -> R.string.progression_stage_uefa_champions_league
            ProgressionStageId.UEFA_EUROPEAN_CHAMPIONSHIP_COMPLETE -> {
                R.string.progression_stage_uefa_european_championship
            }
            ProgressionStageId.UEFA_COMPLETE -> R.string.progression_stage_uefa_complete
            ProgressionStageId.WORLD_COMPLETE -> R.string.progression_stage_world_complete
            ProgressionStageId.VERSUS_VSRM_COMPLETE -> R.string.progression_stage_versus_vsrm
            ProgressionStageId.VERSUS_VSRB_COMPLETE -> R.string.progression_stage_versus_vsrb
            ProgressionStageId.VERSUS_COMPLETE -> R.string.progression_stage_versus_complete
            ProgressionStageId.APP_COMPLETE -> R.string.progression_stage_app_complete
        }
    }
}
