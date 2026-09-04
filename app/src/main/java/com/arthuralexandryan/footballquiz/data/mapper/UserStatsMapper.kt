package com.arthuralexandryan.footballquiz.data.mapper

import com.arthuralexandryan.footballquiz.domain.progression.CompletedStageRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.sync.CloudProgressSnapshot
import com.arthuralexandryan.footballquiz.domain.sync.CloudTop5Scores
import com.arthuralexandryan.footballquiz.domain.sync.CloudUefaScores
import com.arthuralexandryan.footballquiz.models.CompletedStageDTO
import com.arthuralexandryan.footballquiz.models.GameState
import com.arthuralexandryan.footballquiz.models.PlacesTop5DTO
import com.arthuralexandryan.footballquiz.models.PlacesUFADTO
import com.arthuralexandryan.footballquiz.models.ProgressionStateDTO
import com.arthuralexandryan.footballquiz.models.UserStatsDTO

fun UserStatsDTO.toCloudProgressSnapshot(): CloudProgressSnapshot =
    CloudProgressSnapshot(
        id = gameState.id,
        totalScore = gameState.total,
        top5Total = gameState.top5,
        top5Answered = gameState.top5_answered,
        uefaTotal = gameState.ufa,
        uefaAnswered = gameState.ufa_answered,
        worldTotal = gameState.world,
        worldAnswered = gameState.world_answered,
        versusRonaldoMessiTotal = gameState.vsRM,
        versusRonaldoMessiAnswered = gameState.vsRM_answered,
        versusRealBarcelonaTotal = gameState.vsRB,
        versusRealBarcelonaAnswered = gameState.vsRB_answered,
        top5Scores = gameState.top5Scores?.toCloudTop5Scores(),
        uefaScores = gameState.ufaScores?.toCloudUefaScores(),
        completedStages = progression.toCompletedStageRecords(),
        lastSyncedMillis = gameState.lastSynced
    )

fun CloudProgressSnapshot.toGameStateDTO(): GameState =
    GameState(
        id = id,
        total = totalScore,
        top5 = top5Total,
        top5_answered = top5Answered,
        ufa = uefaTotal,
        ufa_answered = uefaAnswered,
        world = worldTotal,
        world_answered = worldAnswered,
        vsRM = versusRonaldoMessiTotal,
        vsRM_answered = versusRonaldoMessiAnswered,
        vsRB = versusRealBarcelonaTotal,
        vsRB_answered = versusRealBarcelonaAnswered,
        top5Scores = top5Scores?.toPlacesTop5DTO(),
        ufaScores = uefaScores?.toPlacesUfaDTO(),
        lastSynced = lastSyncedMillis
    )

private fun PlacesTop5DTO.toCloudTop5Scores(): CloudTop5Scores =
    CloudTop5Scores(
        franceScore = france_score,
        franceAnswered = france_answered,
        germanyScore = germany_score,
        germanyAnswered = germany_answered,
        italyScore = italy_score,
        italyAnswered = italy_answered,
        englandScore = england_score,
        englandAnswered = england_answered,
        spainScore = spain_score,
        spainAnswered = spain_answered
    )

private fun PlacesUFADTO.toCloudUefaScores(): CloudUefaScores =
    CloudUefaScores(
        superCupScore = superCup_score,
        superCupAnswered = superCup_answered,
        europaLeagueScore = europaLeague_score,
        europaLeagueAnswered = europaLeague_answered,
        europeanChampionshipScore = europaChampionship_score,
        europeanChampionshipAnswered = europaChampionship_answered,
        championsLeagueScore = championsLeague_score,
        championsLeagueAnswered = championsLeague_answered
    )

private fun CloudTop5Scores.toPlacesTop5DTO(): PlacesTop5DTO =
    PlacesTop5DTO(
        france_score = franceScore,
        france_answered = franceAnswered,
        germany_score = germanyScore,
        germany_answered = germanyAnswered,
        italy_score = italyScore,
        italy_answered = italyAnswered,
        england_score = englandScore,
        england_answered = englandAnswered,
        spain_score = spainScore,
        spain_answered = spainAnswered
    )

private fun CloudUefaScores.toPlacesUfaDTO(): PlacesUFADTO =
    PlacesUFADTO(
        superCup_score = superCupScore,
        superCup_answered = superCupAnswered,
        europaLeague_score = europaLeagueScore,
        europaLeague_answered = europaLeagueAnswered,
        europaChampionship_score = europeanChampionshipScore,
        europaChampionship_answered = europeanChampionshipAnswered,
        championsLeague_score = championsLeagueScore,
        championsLeague_answered = championsLeagueAnswered
    )

private fun ProgressionStateDTO.toCompletedStageRecords(): List<CompletedStageRecord> {
    return completedStages.mapNotNull { stage ->
        val stageId = ProgressionStageId.entries.firstOrNull { it.value == stage.stageId } ?: return@mapNotNull null
        val achievedAt = stage.achievedAt.takeIf { it > 0L } ?: return@mapNotNull null
        CompletedStageRecord(stageId = stageId, achievedAtMillis = achievedAt)
    }
}

fun List<CompletedStageRecord>.toProgressionStateDTO(): ProgressionStateDTO {
    return ProgressionStateDTO(
        completedStages = map { record ->
            CompletedStageDTO(
                stageId = record.stageId.value,
                achievedAt = record.achievedAtMillis
            )
        }
    )
}
