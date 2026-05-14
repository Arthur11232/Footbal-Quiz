package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.models.UserStatsDTO

interface UserStatsRepository {
    suspend fun deleteStats(userId: String): Result<Unit>
    suspend fun uploadStats(userId: String, stats: UserStatsDTO): Result<Unit>
    suspend fun downloadStats(userId: String): UserStatsDTO?
}
