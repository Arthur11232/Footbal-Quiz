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
        val franceTotal = 40
        val germanyTotal = 40
        val italyTotal = 40
        val englandTotal = 40
        val spainTotal = 40
        val superCupTotal = 20
        val europaLeagueTotal = 20
        val championsLeagueTotal = 20
        val europeanChampionshipTotal = 10
        val vsrmTotal = Constants.VSRM.toIntOrNull() ?: 0
        val vsrbTotal = Constants.VSRB.toIntOrNull() ?: 0
        val top5Total = Constants.Top5.toIntOrNull() ?: 0
        val uefaTotal = Constants.UFA.toIntOrNull() ?: 0
        val worldTotal = Constants.World.toIntOrNull() ?: 0
        val versusTotal = vsrmTotal + vsrbTotal
        val totalQuestions = top5Total + uefaTotal + worldTotal + versusTotal

        val localSnapshot = gameStateLocalDataSource.getProgressSnapshot()
        val franceAnswered = localSnapshot.franceAnswered
        val germanyAnswered = localSnapshot.germanyAnswered
        val italyAnswered = localSnapshot.italyAnswered
        val englandAnswered = localSnapshot.englandAnswered
        val spainAnswered = localSnapshot.spainAnswered
        val superCupAnswered = localSnapshot.superCupAnswered
        val europaLeagueAnswered = localSnapshot.europaLeagueAnswered
        val championsLeagueAnswered = localSnapshot.championsLeagueAnswered
        val europeanChampionshipAnswered = localSnapshot.europeanChampionshipAnswered
        val vsrmAnswered = localSnapshot.vsrmAnswered
        val vsrbAnswered = localSnapshot.vsrbAnswered
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
            franceAnswered = franceAnswered,
            germanyAnswered = germanyAnswered,
            italyAnswered = italyAnswered,
            englandAnswered = englandAnswered,
            spainAnswered = spainAnswered,
            superCupAnswered = superCupAnswered,
            europaLeagueAnswered = europaLeagueAnswered,
            championsLeagueAnswered = championsLeagueAnswered,
            europeanChampionshipAnswered = europeanChampionshipAnswered,
            vsrmAnswered = vsrmAnswered,
            vsrbAnswered = vsrbAnswered,
            top5Answered = top5Answered,
            uefaAnswered = uefaAnswered,
            worldAnswered = worldAnswered,
            versusAnswered = versusAnswered,
            totalAnswered = totalAnswered,
            franceTotal = franceTotal,
            germanyTotal = germanyTotal,
            italyTotal = italyTotal,
            englandTotal = englandTotal,
            spainTotal = spainTotal,
            superCupTotal = superCupTotal,
            europaLeagueTotal = europaLeagueTotal,
            championsLeagueTotal = championsLeagueTotal,
            europeanChampionshipTotal = europeanChampionshipTotal,
            vsrmTotal = vsrmTotal,
            vsrbTotal = vsrbTotal,
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
            .apply {
                com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId.entries
                    .forEach {
                        remove(Constants.ProgressionStageAchievedAtKeyPrefix + it.value)
                        remove(Constants.CertificateIssuedAtKeyPrefix + it.value)
                        remove(Constants.CertificateIdKeyPrefix + it.value)
                        remove(Constants.CertificateRecipientNameKeyPrefix + it.value)
                    }
            }
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
