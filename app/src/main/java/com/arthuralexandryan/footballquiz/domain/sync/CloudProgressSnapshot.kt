package com.arthuralexandryan.footballquiz.domain.sync

import com.arthuralexandryan.footballquiz.domain.progression.CompletedStageRecord

data class CloudProgressSnapshot(
    val id: Int,
    val totalScore: Int,
    val top5Total: Int,
    val top5Answered: Int,
    val uefaTotal: Int,
    val uefaAnswered: Int,
    val worldTotal: Int,
    val worldAnswered: Int,
    val versusRonaldoMessiTotal: Int,
    val versusRonaldoMessiAnswered: Int,
    val versusRealBarcelonaTotal: Int,
    val versusRealBarcelonaAnswered: Int,
    val top5Scores: CloudTop5Scores? = null,
    val uefaScores: CloudUefaScores? = null,
    val completedStages: List<CompletedStageRecord> = emptyList(),
    val lastSyncedMillis: Long
) {
    val hasProgress: Boolean
        get() = totalScore > 0 ||
            top5Answered > 0 ||
            uefaAnswered > 0 ||
            worldAnswered > 0 ||
            versusRonaldoMessiAnswered > 0 ||
            versusRealBarcelonaAnswered > 0
}

data class CloudTop5Scores(
    val franceScore: Int,
    val franceAnswered: Int,
    val germanyScore: Int,
    val germanyAnswered: Int,
    val italyScore: Int,
    val italyAnswered: Int,
    val englandScore: Int,
    val englandAnswered: Int,
    val spainScore: Int,
    val spainAnswered: Int
)

data class CloudUefaScores(
    val superCupScore: Int,
    val superCupAnswered: Int,
    val europaLeagueScore: Int,
    val europaLeagueAnswered: Int,
    val europeanChampionshipScore: Int,
    val europeanChampionshipAnswered: Int,
    val championsLeagueScore: Int,
    val championsLeagueAnswered: Int
)
