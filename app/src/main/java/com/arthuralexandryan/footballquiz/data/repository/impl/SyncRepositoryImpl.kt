package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.CloudSyncManager
import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SyncRepositoryImpl(
    private val context: Context,
    private val dbHelper: DB_Helper
) : SyncRepository {

    override suspend fun resolveSyncDecision(user: FirebaseUser): SyncRepository.SyncDecision {
        return suspendCancellableCoroutine { continuation ->
            CloudSyncManager.resolveSyncDecision(context, dbHelper, user) { decision ->
                val mappedDecision = when (decision) {
                    is CloudSyncManager.SyncDecision.None -> SyncRepository.SyncDecision.None
                    is CloudSyncManager.SyncDecision.RestoreCloud -> SyncRepository.SyncDecision.RestoreCloud(
                        userId = user.uid,
                        cloudStats = decision.cloudStats
                    )
                    is CloudSyncManager.SyncDecision.UploadLocal -> SyncRepository.SyncDecision.UploadLocal(decision.user)
                }
                continuation.resume(mappedDecision)
            }
        }
    }

    override suspend fun uploadLocalStats(user: FirebaseUser): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            CloudSyncManager.uploadLocalStats(context, user) { success, error ->
                if (success) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(
                        Result.failure(
                            IllegalStateException(error ?: "Cloud sync upload failed")
                        )
                    )
                }
            }
        }
    }

    override fun restoreCloudStats(userId: String, cloudStats: UserStatsDTO) {
        CloudSyncManager.restoreCloudStats(context, dbHelper, userId, cloudStats)
    }
}
