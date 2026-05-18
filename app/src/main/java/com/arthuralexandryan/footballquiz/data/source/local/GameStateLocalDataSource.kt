package com.arthuralexandryan.footballquiz.data.source.local

import com.arthuralexandryan.footballquiz.models.GameState
import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import com.google.firebase.auth.FirebaseUser

data class LocalProgressSnapshot(
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
    fun getUserStats(user: FirebaseUser): UserStatsDTO
    fun resetScoresToDefault()
    fun restoreStats(gameState: GameState)
    suspend fun resetAllProgress(): Result<Unit>
}
