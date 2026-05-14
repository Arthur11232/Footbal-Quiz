package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import android.net.Uri
import com.arthuralexandryan.footballquiz.data.repository.GameProgressRepository
import com.arthuralexandryan.footballquiz.data.repository.ProfileProgressSnapshot
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.interfaces.Check
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

class GameProgressRepositoryImpl(
    private val context: Context,
    private val dbHelper: DB_Helper,
    private val settingsRepository: com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
) : GameProgressRepository {

    override fun getProgressSnapshot(): ProfileProgressSnapshot {
        val top5Total = Constants.Top5.toIntOrNull() ?: 0
        val uefaTotal = Constants.UFA.toIntOrNull() ?: 0
        val worldTotal = Constants.World.toIntOrNull() ?: 0
        val versusTotal = (Constants.VSRM.toIntOrNull() ?: 0) + (Constants.VSRB.toIntOrNull() ?: 0)
        val totalQuestions = top5Total + uefaTotal + worldTotal + versusTotal

        val top5Answered = dbHelper.top5AnsweredScores
        val uefaAnswered = dbHelper.getUFAAnsweredScores()
        val worldAnswered = dbHelper.getWorldAnsweredScores()
        val versusAnswered = dbHelper.getVersusAnsweredScores()
        val totalAnswered = top5Answered + uefaAnswered + worldAnswered + versusAnswered
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
            totalScore = dbHelper.getTotalScore()
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

        return suspendCancellableCoroutine { continuation ->
            dbHelper.deleteAll(object : Check {
                override fun onCheck() {
                    continuation.resume(Result.success(Unit))
                }
            }) {
                continuation.resume(Result.failure(it))
            }
        }
    }

    override fun markAutoRestorePromptDismissed(userId: String) {
        Prefer.setBooleanPreference(
            context,
            Constants.UserStatsRestorePromptDismissedKeyPrefix + userId,
            true
        )
    }
}
