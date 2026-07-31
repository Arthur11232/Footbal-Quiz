package com.arthuralexandryan.footballquiz.data.repository

import android.net.Uri
import com.arthuralexandryan.footballquiz.domain.progression.ProfileProgressSnapshot

interface GameProgressRepository {
    fun getProgressSnapshot(): ProfileProgressSnapshot
    fun getProfilePhotoPath(userId: String): String?
    suspend fun saveProfilePhoto(userId: String, uri: Uri): Result<String>
    suspend fun clearLocalUserData(userId: String): Result<Unit>
    fun markAutoRestorePromptDismissed(userId: String)
}
