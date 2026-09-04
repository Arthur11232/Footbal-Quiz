package com.arthuralexandryan.footballquiz.data.source.local

import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import com.arthuralexandryan.footballquiz.models.GameState
import com.arthuralexandryan.footballquiz.models.UserStatsDTO

data class LocalProgressSnapshot(
    val franceAnswered: Int,
    val germanyAnswered: Int,
    val italyAnswered: Int,
    val englandAnswered: Int,
    val spainAnswered: Int,
    val superCupAnswered: Int,
    val europaLeagueAnswered: Int,
    val championsLeagueAnswered: Int,
    val europeanChampionshipAnswered: Int,
    val vsrmAnswered: Int,
    val vsrbAnswered: Int,
    val top5Answered: Int,
    val uefaAnswered: Int,
    val worldAnswered: Int,
    val versusAnswered: Int,
    val totalScore: Int
) {
    val totalAnswered: Int
        get() = top5Answered + uefaAnswered + worldAnswered + versusAnswered

    val hasProgress: Boolean
        get() = totalScore > 0 || totalAnswered > 0
}

interface GameStateLocalDataSource {
    fun getProgressSnapshot(): LocalProgressSnapshot
    fun getUserStats(user: AuthenticatedUser): UserStatsDTO
    fun resetScoresToDefault()
    fun restoreStats(gameState: GameState)
    suspend fun resetAllProgress(): Result<Unit>
}
