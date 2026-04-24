package com.arthuralexandryan.footballquiz.models

import android.content.Context
import android.util.Log
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import com.google.firebase.auth.FirebaseUser
import io.realm.Realm

object CloudSyncManager {

    sealed class SyncDecision {
        data object None : SyncDecision()
        data class RestoreCloud(val cloudStats: UserStatsDTO) : SyncDecision()
        data class UploadLocal(val user: FirebaseUser) : SyncDecision()
    }

    data class LocalStatsSnapshot(
        val totalScore: Int,
        val totalAnswered: Int,
        val lastSynced: Long
    ) {
        val hasProgress: Boolean
            get() = totalScore > 0 || totalAnswered > 0
    }

    fun getLocalSnapshot(context: Context, dbHelper: DB_Helper, userId: String): LocalStatsSnapshot {
        val totalAnswered = dbHelper.top5AnsweredScores +
            dbHelper.getUFAAnsweredScores() +
            dbHelper.getWorldAnsweredScores() +
            dbHelper.getVersusAnsweredScores()

        return LocalStatsSnapshot(
            totalScore = dbHelper.getTotalScore(),
            totalAnswered = totalAnswered,
            lastSynced = getLocalSyncTimestamp(context, userId)
        )
    }

    fun resolveSyncDecision(
        context: Context,
        dbHelper: DB_Helper,
        user: FirebaseUser,
        onComplete: (SyncDecision) -> Unit
    ) {
        val localSnapshot = getLocalSnapshot(context, dbHelper, user.uid)
        Log.d(
            "FQ_Log",
            "CloudSyncManager.resolveSyncDecision: user=${user.uid}, localScore=${localSnapshot.totalScore}, localAnswered=${localSnapshot.totalAnswered}, localLastSynced=${localSnapshot.lastSynced}"
        )

        UserStatsService.getInstance().downloadStats(user.uid) { cloudStats ->
            if (cloudStats == null || !cloudStats.hasProgress()) {
                Log.d("FQ_Log", "CloudSyncManager.resolveSyncDecision: no cloud progress for user=${user.uid}")
                onComplete(
                    if (localSnapshot.hasProgress) SyncDecision.UploadLocal(user) else SyncDecision.None
                )
                return@downloadStats
            }

            if (!localSnapshot.hasProgress) {
                Log.d("FQ_Log", "CloudSyncManager.resolveSyncDecision: local progress empty, restore from cloud for user=${user.uid}")
                onComplete(SyncDecision.RestoreCloud(cloudStats))
                return@downloadStats
            }

            val cloudScore = cloudStats.gameState.total
            val localScore = localSnapshot.totalScore
            val cloudLastSynced = cloudStats.gameState.lastSynced
            Log.d(
                "FQ_Log",
                "CloudSyncManager.resolveSyncDecision: user=${user.uid}, cloudScore=$cloudScore, cloudLastSynced=$cloudLastSynced"
            )

            when {
                cloudScore > localScore -> {
                    Log.d("FQ_Log", "CloudSyncManager.resolveSyncDecision: decision=RestoreCloud by score for user=${user.uid}")
                    onComplete(SyncDecision.RestoreCloud(cloudStats))
                }
                localScore > cloudScore -> {
                    Log.d("FQ_Log", "CloudSyncManager.resolveSyncDecision: decision=UploadLocal by score for user=${user.uid}")
                    onComplete(SyncDecision.UploadLocal(user))
                }
                cloudLastSynced > localSnapshot.lastSynced -> {
                    Log.d("FQ_Log", "CloudSyncManager.resolveSyncDecision: decision=RestoreCloud by timestamp for user=${user.uid}")
                    onComplete(SyncDecision.RestoreCloud(cloudStats))
                }
                localSnapshot.lastSynced > cloudLastSynced -> {
                    Log.d("FQ_Log", "CloudSyncManager.resolveSyncDecision: decision=UploadLocal by timestamp for user=${user.uid}")
                    onComplete(SyncDecision.UploadLocal(user))
                }
                else -> {
                    Log.d("FQ_Log", "CloudSyncManager.resolveSyncDecision: decision=None for user=${user.uid}")
                    onComplete(SyncDecision.None)
                }
            }
        }
    }

    fun uploadLocalStats(
        context: Context,
        user: FirebaseUser,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val realm = Realm.getDefaultInstance()
        val scores = realm.where(FQ_Scores::class.java).findFirst()

        if (scores == null) {
            Log.e("FQ_Log", "CloudSyncManager.uploadLocalStats: no local stats for user=${user.uid}")
            realm.close()
            onComplete(false, "No local stats")
            return
        }

        val statsDto = scores.toDTO(user)
        val syncTimestamp = statsDto.gameState.lastSynced
        Log.d("FQ_Log", "CloudSyncManager.uploadLocalStats: uploading total=${statsDto.gameState.total} for user=${user.uid}")

        UserStatsService.getInstance().uploadStats(user.uid, statsDto) { success ->
            if (success) {
                setLocalSyncTimestamp(context, user.uid, syncTimestamp)
            }
            Log.d("FQ_Log", "CloudSyncManager.uploadLocalStats: success=$success for user=${user.uid}")
            realm.close()
            onComplete(if (success) true else false, if (success) null else "Upload failed")
        }
    }

    fun restoreCloudStats(
        context: Context,
        dbHelper: DB_Helper,
        userId: String,
        cloudStats: UserStatsDTO
    ) {
        Log.d(
            "FQ_Log",
            "CloudSyncManager.restoreCloudStats: restoring total=${cloudStats.gameState.total}, lastSynced=${cloudStats.gameState.lastSynced} for user=$userId"
        )
        dbHelper.restoreStats(cloudStats.gameState)
        Prefer.setBooleanPreference(context, "isFirstPlay", false)
        Prefer.setBooleanPreference(context, Constant.INIT_DB, true)
        setLocalSyncTimestamp(context, userId, cloudStats.gameState.lastSynced)
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

    private fun getLocalSyncTimestamp(context: Context, userId: String): Long {
        return Prefer.getLongPreference(
            context,
            Constants.UserStatsLastSyncKeyPrefix + userId,
            0L
        )
    }

    private fun setLocalSyncTimestamp(context: Context, userId: String, timestamp: Long) {
        Prefer.setLongPreference(
            context,
            Constants.UserStatsLastSyncKeyPrefix + userId,
            timestamp
        )
    }
}
