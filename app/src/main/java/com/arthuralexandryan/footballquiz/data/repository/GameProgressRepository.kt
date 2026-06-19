package com.arthuralexandryan.footballquiz.data.repository

import android.net.Uri

data class ProfileProgressSnapshot(
    val franceAnswered: Int,
    val germanyAnswered: Int,
    val italyAnswered: Int,
    val englandAnswered: Int,
    val spainAnswered: Int,
    val superCupAnswered: Int,
    val europaLeagueAnswered: Int,
    val championsLeagueAnswered: Int,
    val europeanChampionshipAnswered: Int,
    val vsrmAnswered: Int,
    val vsrbAnswered: Int,
    val top5Answered: Int,
    val uefaAnswered: Int,
    val worldAnswered: Int,
    val versusAnswered: Int,
    val totalAnswered: Int,
    val franceTotal: Int,
    val germanyTotal: Int,
    val italyTotal: Int,
    val englandTotal: Int,
    val spainTotal: Int,
    val superCupTotal: Int,
    val europaLeagueTotal: Int,
    val championsLeagueTotal: Int,
    val europeanChampionshipTotal: Int,
    val vsrmTotal: Int,
    val vsrbTotal: Int,
    val top5Total: Int,
    val uefaTotal: Int,
    val worldTotal: Int,
    val versusTotal: Int,
    val totalQuestions: Int,
    val progressPercent: Int,
    val totalScore: Int
)

interface GameProgressRepository {
    fun getProgressSnapshot(): ProfileProgressSnapshot
    fun getProfilePhotoPath(userId: String): String?
    suspend fun saveProfilePhoto(userId: String, uri: Uri): Result<String>
    suspend fun clearLocalUserData(userId: String): Result<Unit>
    fun markAutoRestorePromptDismissed(userId: String)
}
