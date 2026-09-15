package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import com.arthuralexandryan.footballquiz.models.UserStatsDTO

interface SyncRepository {

    sealed class SyncDecision {
        data object None : SyncDecision()
        data class AutoRestoreCloud(
            val userId: String,
            val cloudStats: UserStatsDTO
        ) : SyncDecision()
        data class PromptConflict(
            val userId: String,
            val cloudStats: UserStatsDTO
        ) : SyncDecision()
        data class UploadLocal(
            val user: AuthenticatedUser
        ) : SyncDecision()
    }

    suspend fun resolveSyncDecision(user: AuthenticatedUser): SyncDecision

    suspend fun getCloudStats(userId: String): UserStatsDTO?

    suspend fun uploadLocalStats(user: AuthenticatedUser): Result<Unit>

    suspend fun restoreCloudStats(userId: String, cloudStats: UserStatsDTO)

    suspend fun deleteCloudStats(userId: String): Result<Unit>
}
