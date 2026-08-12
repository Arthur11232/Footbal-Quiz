package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import com.arthuralexandryan.footballquiz.domain.sync.CloudProgressSnapshot

interface SyncRepository {

    sealed class SyncDecision {
        data object None : SyncDecision()
        data class AutoRestoreCloud(
            val userId: String,
            val cloudProgress: CloudProgressSnapshot
        ) : SyncDecision()
        data class PromptConflict(
            val userId: String,
            val cloudProgress: CloudProgressSnapshot
        ) : SyncDecision()
        data class UploadLocal(
            val user: AuthenticatedUser
        ) : SyncDecision()
    }

    suspend fun resolveSyncDecision(user: AuthenticatedUser): SyncDecision

    suspend fun downloadCloudProgress(userId: String): CloudProgressSnapshot?

    suspend fun uploadLocalStats(user: AuthenticatedUser): Result<Unit>

    suspend fun restoreCloudProgress(userId: String, cloudProgress: CloudProgressSnapshot)

    suspend fun deleteCloudProgress(userId: String): Result<Unit>
}
