package com.arthuralexandryan.footballquiz.data.repository

import android.net.Uri

data class ProfileProgressSnapshot(
    val top5Answered: Int,
    val uefaAnswered: Int,
    val worldAnswered: Int,
    val versusAnswered: Int,
    val totalAnswered: Int,
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
