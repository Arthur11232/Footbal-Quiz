package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import com.google.firebase.auth.FirebaseUser

interface SyncRepository {

    sealed class SyncDecision {
        data object None : SyncDecision()
        data class RestoreCloud(
            val userId: String,
            val cloudStats: UserStatsDTO
        ) : SyncDecision()
        data class UploadLocal(
            val user: FirebaseUser
        ) : SyncDecision()
    }

    suspend fun resolveSyncDecision(user: FirebaseUser): SyncDecision

    suspend fun uploadLocalStats(user: FirebaseUser): Result<Unit>

    suspend fun restoreCloudStats(userId: String, cloudStats: UserStatsDTO)
}
