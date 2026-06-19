package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.ProgressionRepository
import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.data.repository.UserStatsRepository
import com.arthuralexandryan.footballquiz.data.source.local.GameStateLocalDataSource
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import com.arthuralexandryan.footballquiz.models.toCompletedStageRecords
import com.arthuralexandryan.footballquiz.models.toProgressionStateDTO
import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import com.google.firebase.auth.FirebaseUser

class SyncRepositoryImpl(
    private val context: Context,
    private val gameStateLocalDataSource: GameStateLocalDataSource,
    private val userStatsRepository: UserStatsRepository,
    private val progressionRepository: ProgressionRepository
) : SyncRepository {

    override suspend fun resolveSyncDecision(user: FirebaseUser): SyncRepository.SyncDecision {
        val localSnapshot = gameStateLocalDataSource.getProgressSnapshot()
        val cloudStats = userStatsRepository.downloadStats(user.uid)

        if (cloudStats == null || !cloudStats.hasProgress()) {
            return if (localSnapshot.hasProgress) {
                SyncRepository.SyncDecision.UploadLocal(user)
            } else {
                SyncRepository.SyncDecision.None
            }
        }

        if (!localSnapshot.hasProgress) {
            return SyncRepository.SyncDecision.RestoreCloud(
                userId = user.uid,
                cloudStats = cloudStats
            )
        }

        val cloudScore = cloudStats.gameState.total
        val localScore = localSnapshot.totalScore
        val cloudLastSynced = cloudStats.gameState.lastSynced
        val localLastSynced = getLocalSyncTimestamp(user.uid)

        return when {
            cloudScore > localScore -> SyncRepository.SyncDecision.RestoreCloud(user.uid, cloudStats)
            localScore > cloudScore -> SyncRepository.SyncDecision.UploadLocal(user)
            cloudLastSynced > localLastSynced -> SyncRepository.SyncDecision.RestoreCloud(user.uid, cloudStats)
            localLastSynced > cloudLastSynced -> SyncRepository.SyncDecision.UploadLocal(user)
            else -> SyncRepository.SyncDecision.None
        }
    }

    override suspend fun uploadLocalStats(user: FirebaseUser): Result<Unit> {
        val stats = gameStateLocalDataSource.getUserStats(user).copy(
            progression = progressionRepository.getCompletedStageRecords().toProgressionStateDTO()
        )
        return userStatsRepository.uploadStats(user.uid, stats).onSuccess {
            setLocalSyncTimestamp(user.uid, stats.gameState.lastSynced)
        }
    }

    override fun restoreCloudStats(userId: String, cloudStats: UserStatsDTO) {
        gameStateLocalDataSource.restoreStats(cloudStats.gameState)
        progressionRepository.replaceCompletedStageRecords(cloudStats.toCompletedStageRecords())
        Prefer.setBooleanPreference(context, "isFirstPlay", false)
        Prefer.setBooleanPreference(context, Constant.INIT_DB, true)
        setLocalSyncTimestamp(userId, cloudStats.gameState.lastSynced)
    }

    private fun UserStatsDTO.hasProgress(): Boolean {
        val state = gameState
        return state.total > 0 ||
            state.top5_answered > 0 ||
            state.ufa_answered > 0 ||
            state.world_answered > 0 ||
            state.vsRM_answered > 0 ||
            state.vsRB_answered > 0
    }

    private fun getLocalSyncTimestamp(userId: String): Long {
        return Prefer.getLongPreference(
            context,
            Constants.UserStatsLastSyncKeyPrefix + userId,
            0L
        )
    }

    private fun setLocalSyncTimestamp(userId: String, timestamp: Long) {
        Prefer.setLongPreference(
            context,
            Constants.UserStatsLastSyncKeyPrefix + userId,
            timestamp
        )
    }
}
