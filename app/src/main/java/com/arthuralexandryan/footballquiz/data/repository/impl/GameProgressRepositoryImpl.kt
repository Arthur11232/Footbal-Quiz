package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import android.net.Uri
import com.arthuralexandryan.footballquiz.data.repository.GameProgressRepository
import com.arthuralexandryan.footballquiz.data.repository.ProfileProgressSnapshot
import com.arthuralexandryan.footballquiz.data.source.local.GameStateLocalDataSource
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import java.io.File
import java.io.FileOutputStream

class GameProgressRepositoryImpl(
    private val context: Context,
    private val gameStateLocalDataSource: GameStateLocalDataSource,
    private val settingsRepository: com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
) : GameProgressRepository {

    override fun getProgressSnapshot(): ProfileProgressSnapshot {
        val top5Total = Constants.Top5.toIntOrNull() ?: 0
        val uefaTotal = Constants.UFA.toIntOrNull() ?: 0
        val worldTotal = Constants.World.toIntOrNull() ?: 0
        val versusTotal = (Constants.VSRM.toIntOrNull() ?: 0) + (Constants.VSRB.toIntOrNull() ?: 0)
        val totalQuestions = top5Total + uefaTotal + worldTotal + versusTotal

        val localSnapshot = gameStateLocalDataSource.getProgressSnapshot()
        val top5Answered = localSnapshot.top5Answered
        val uefaAnswered = localSnapshot.uefaAnswered
        val worldAnswered = localSnapshot.worldAnswered
        val versusAnswered = localSnapshot.versusAnswered
        val totalAnswered = localSnapshot.totalAnswered
        val progressPercent = if (totalQuestions > 0) {
            (totalAnswered * 100) / totalQuestions
        } else {
            0
        }

        return ProfileProgressSnapshot(
            top5Answered = top5Answered,
            uefaAnswered = uefaAnswered,
            worldAnswered = worldAnswered,
            versusAnswered = versusAnswered,
            totalAnswered = totalAnswered,
            top5Total = top5Total,
            uefaTotal = uefaTotal,
            worldTotal = worldTotal,
            versusTotal = versusTotal,
            totalQuestions = totalQuestions,
            progressPercent = progressPercent,
            totalScore = localSnapshot.totalScore
        )
    }

    override fun getProfilePhotoPath(userId: String): String? {
        return Prefer.getStringPreference(context, Constants.UserPhotoKey + userId, null)
    }

    override suspend fun saveProfilePhoto(userId: String, uri: Uri): Result<String> {
        return runCatching {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: error("Unable to open image stream")
            val file = File(context.filesDir, "profile_$userId.jpg")
            FileOutputStream(file).use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }
            Prefer.setStringPreference(context, Constants.UserPhotoKey + userId, file.absolutePath)
            file.absolutePath
        }
    }

    override suspend fun clearLocalUserData(userId: String): Result<Unit> {
        val photoPath = getProfilePhotoPath(userId)
        if (photoPath != null) {
            File(photoPath).delete()
        }
        Prefer.getSharedPreferenceEditor(context)
            .remove(Constants.UserPhotoKey + userId)
            .remove(Constants.UserStatsLastSyncKeyPrefix + userId)
            .remove(Constants.UserStatsRestorePromptDismissedKeyPrefix + userId)
            .putBoolean("isFirstPlay", true)
            .apply()
        settingsRepository.setFirstPlay(true)
        return gameStateLocalDataSource.resetAllProgress()
    }

    override fun markAutoRestorePromptDismissed(userId: String) {
        Prefer.setBooleanPreference(
            context,
            Constants.UserStatsRestorePromptDismissedKeyPrefix + userId,
            true
        )
    }
}
