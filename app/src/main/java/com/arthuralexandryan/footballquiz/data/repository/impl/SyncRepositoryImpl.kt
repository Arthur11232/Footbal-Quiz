package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.CertificateRepository
import com.arthuralexandryan.footballquiz.data.repository.ProgressionRepository
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.data.repository.UserStatsRepository
import com.arthuralexandryan.footballquiz.data.source.local.GameStateLocalDataSource
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord
import com.arthuralexandryan.footballquiz.models.toCompletedStageRecords
import com.arthuralexandryan.footballquiz.models.toProgressionStateDTO
import com.arthuralexandryan.footballquiz.models.UserStatsDTO

class SyncRepositoryImpl(
    private val context: Context,
    private val gameStateLocalDataSource: GameStateLocalDataSource,
    private val userStatsRepository: UserStatsRepository,
    private val progressionRepository: ProgressionRepository,
    private val certificateRepository: CertificateRepository,
    private val settingsRepository: SettingsRepository
) : SyncRepository {

    override suspend fun resolveSyncDecision(user: AuthenticatedUser): SyncRepository.SyncDecision {
        val localSnapshot = gameStateLocalDataSource.getProgressSnapshot()
        val cloudStats = userStatsRepository.downloadStats(user.id)
        val remoteCertificates = certificateRepository.downloadIssuedCertificates(user.id)
        mergeRemoteCertificatesIntoLocal(remoteCertificates)
        val localHasProgress = localSnapshot.hasProgress

        if (cloudStats == null || !cloudStats.hasProgress()) {
            return if (localHasProgress) {
                SyncRepository.SyncDecision.UploadLocal(user)
            } else {
                SyncRepository.SyncDecision.None
            }
        }

        if (!localHasProgress) {
            return SyncRepository.SyncDecision.AutoRestoreCloud(
                userId = user.id,
                cloudStats = cloudStats
            )
        }

        val cloudScore = cloudStats.gameState.total
        val localScore = localSnapshot.totalScore
        val cloudLastSynced = cloudStats.gameState.lastSynced
        val localLastSynced = getLocalSyncTimestamp(user.id)

        return if (cloudScore == localScore && cloudLastSynced == localLastSynced) {
            SyncRepository.SyncDecision.None
        } else {
            SyncRepository.SyncDecision.PromptConflict(user.id, cloudStats)
        }
    }

    override suspend fun uploadLocalStats(user: AuthenticatedUser): Result<Unit> {
        val stats = gameStateLocalDataSource.getUserStats(user).copy(
            progression = progressionRepository.getCompletedStageRecords().toProgressionStateDTO()
        )
        val statsUploadResult = userStatsRepository.uploadStats(user.id, stats)
        if (statsUploadResult.isFailure) {
            return statsUploadResult
        }

        val certificatesUploadResult = certificateRepository.uploadIssuedCertificates(
            userId = user.id,
            recipientName = user.recipientName(),
            userEmail = user.email,
            records = certificateRepository.getIssuedCertificates()
        )
        if (certificatesUploadResult.isFailure) {
            return certificatesUploadResult
        }

        setLocalSyncTimestamp(user.id, stats.gameState.lastSynced)
        settingsRepository.setAutoRestorePromptDismissed(user.id, false)
        return Result.success(Unit)
    }

    override suspend fun restoreCloudStats(userId: String, cloudStats: UserStatsDTO) {
        gameStateLocalDataSource.restoreStats(cloudStats.gameState)
        progressionRepository.replaceCompletedStageRecords(cloudStats.toCompletedStageRecords())
        val remoteCertificates = certificateRepository.downloadIssuedCertificates(userId)
        mergeRemoteCertificatesIntoLocal(remoteCertificates)
        Prefer.setBooleanPreference(context, "isFirstPlay", false)
        Prefer.setBooleanPreference(context, Constant.INIT_DB, true)
        setLocalSyncTimestamp(userId, cloudStats.gameState.lastSynced)
        settingsRepository.setAutoRestorePromptDismissed(userId, false)
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

    private fun mergeRemoteCertificatesIntoLocal(remoteCertificates: List<IssuedCertificateRecord>) {
        if (remoteCertificates.isEmpty()) return
        val mergedByStageId = buildMap {
            certificateRepository.getIssuedCertificates().forEach { put(it.stageId, it) }
            remoteCertificates.forEach { record ->
                val existing = get(record.stageId)
                if (existing == null || existing.issuedAtMillis <= 0L) {
                    put(record.stageId, record)
                }
            }
        }
        certificateRepository.saveIssuedCertificates(mergedByStageId.values.toList())
    }
}
