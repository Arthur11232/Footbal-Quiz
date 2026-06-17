package com.arthuralexandryan.footballquiz.data.repository.impl

import com.arthuralexandryan.footballquiz.data.repository.UserStatsRepository
import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import com.arthuralexandryan.footballquiz.models.UserStatsService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class UserStatsRepositoryImpl(
    private val userStatsService: UserStatsService
) : UserStatsRepository {

    override suspend fun deleteStats(userId: String): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            userStatsService.deleteStats(userId) { success ->
                if (success) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(IllegalStateException("Failed to delete cloud stats")))
                }
            }
        }
    }

    override suspend fun uploadStats(userId: String, stats: UserStatsDTO): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            userStatsService.uploadStats(userId, stats) { success ->
                if (success) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(IllegalStateException("Failed to upload cloud stats")))
                }
            }
        }
    }

    override suspend fun downloadStats(userId: String): UserStatsDTO? {
        return suspendCancellableCoroutine { continuation ->
            userStatsService.downloadStats(userId) { stats ->
                continuation.resume(stats)
            }
        }
    }
}
