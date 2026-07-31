package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser

interface NotificationRepository {
    suspend fun syncSignedInUser(user: AuthenticatedUser): Result<Unit>
    suspend fun handleNewToken(token: String): Result<Unit>
    suspend fun unbindCurrentDevice(userId: String?): Result<Unit>
    suspend fun deleteDevices(userId: String): Result<Unit>
    fun clearLocalSession()

    fun areNotificationsEnabled(): Boolean
    fun areProgressNotificationsEnabled(): Boolean
    fun areRewardNotificationsEnabled(): Boolean
    fun areUpdateNotificationsEnabled(): Boolean

    fun setNotificationsEnabled(enabled: Boolean)
    fun setProgressNotificationsEnabled(enabled: Boolean)
    fun setRewardNotificationsEnabled(enabled: Boolean)
    fun setUpdateNotificationsEnabled(enabled: Boolean)

    fun hasNotificationPermission(): Boolean
    fun shouldShowPermissionPrompt(userId: String): Boolean
    fun markPermissionPromptShown(userId: String)
    fun getBoundUserId(): String?
}
