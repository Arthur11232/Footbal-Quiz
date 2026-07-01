package com.arthuralexandryan.footballquiz.data.repository.impl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.arthuralexandryan.footballquiz.BuildConfig
import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.models.UserNotificationDeviceDTO
import com.arthuralexandryan.footballquiz.models.UserStatsService
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class NotificationRepositoryImpl(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val userStatsService: UserStatsService,
    private val authRepository: AuthRepository
) : NotificationRepository {

    override suspend fun syncSignedInUser(user: FirebaseUser): Result<Unit> {
        val previousBoundUserId = settingsRepository.getNotificationBoundUserId()
        if (!previousBoundUserId.isNullOrBlank() && previousBoundUserId != user.uid) {
            val unbindResult = unbindCurrentDevice(previousBoundUserId)
            if (unbindResult.isFailure) {
                return unbindResult
            }
        }

        val token = resolveCurrentToken()
            ?: return Result.failure(IllegalStateException("FCM token is unavailable"))
        return uploadDevice(user, token)
    }

    override suspend fun handleNewToken(token: String): Result<Unit> {
        if (token.isBlank()) {
            return Result.failure(IllegalArgumentException("FCM token is blank"))
        }
        settingsRepository.setCachedNotificationToken(token)

        val currentUser = authRepository.getCurrentUser()
            ?: return Result.success(Unit)

        return uploadDevice(currentUser, token)
    }

    override suspend fun unbindCurrentDevice(userId: String?): Result<Unit> {
        val resolvedUserId = userId
            ?: settingsRepository.getNotificationBoundUserId()
            ?: return Result.success(Unit)

        val installationId = settingsRepository.getNotificationInstallationId()
        val result = suspendCancellableCoroutine<Result<Unit>> { continuation ->
            userStatsService.deactivateNotificationDevice(
                userId = resolvedUserId,
                installationId = installationId,
                updatedAtMillis = System.currentTimeMillis()
            ) { success ->
                continuation.resume(
                    if (success) {
                        Result.success(Unit)
                    } else {
                        Result.failure(IllegalStateException("Failed to deactivate notification device"))
                    }
                )
            }
        }
        if (result.isSuccess) {
            settingsRepository.setNotificationBoundUserId(null)
            settingsRepository.setNotificationLastSyncedAt(0L)
        }
        return result
    }

    override suspend fun deleteDevices(userId: String): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            userStatsService.deleteNotificationDevices(userId) { success ->
                continuation.resume(
                    if (success) {
                        Result.success(Unit)
                    } else {
                        Result.failure(IllegalStateException("Failed to delete notification devices"))
                    }
                )
            }
        }
    }

    override fun clearLocalSession() {
        settingsRepository.setNotificationBoundUserId(null)
        settingsRepository.setNotificationLastSyncedAt(0L)
    }

    override fun areNotificationsEnabled(): Boolean =
        settingsRepository.areNotificationsEnabled()

    override fun areProgressNotificationsEnabled(): Boolean =
        settingsRepository.areProgressNotificationsEnabled()

    override fun areRewardNotificationsEnabled(): Boolean =
        settingsRepository.areRewardNotificationsEnabled()

    override fun areUpdateNotificationsEnabled(): Boolean =
        settingsRepository.areUpdateNotificationsEnabled()

    override fun setNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
    }

    override fun setProgressNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setProgressNotificationsEnabled(enabled)
    }

    override fun setRewardNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setRewardNotificationsEnabled(enabled)
    }

    override fun setUpdateNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setUpdateNotificationsEnabled(enabled)
    }

    override fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun shouldShowPermissionPrompt(userId: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        if (!authRepository.isUserSignedIn()) return false
        if (!areNotificationsEnabled()) return false
        if (hasNotificationPermission()) return false
        return !settingsRepository.isNotificationPermissionPromptShown(userId)
    }

    override fun markPermissionPromptShown(userId: String) {
        settingsRepository.setNotificationPermissionPromptShown(userId, true)
    }

    override fun getBoundUserId(): String? =
        settingsRepository.getNotificationBoundUserId()

    private suspend fun uploadDevice(user: FirebaseUser, token: String): Result<Unit> {
        val now = System.currentTimeMillis()
        val payload = UserNotificationDeviceDTO(
            installationId = settingsRepository.getNotificationInstallationId(),
            fcmToken = token,
            platform = "android",
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE.toLong(),
            locale = settingsRepository.getSelectedLocale(),
            userEmail = user.email.orEmpty(),
            notificationsEnabled = areNotificationsEnabled(),
            progressNotificationsEnabled = areProgressNotificationsEnabled(),
            rewardNotificationsEnabled = areRewardNotificationsEnabled(),
            updatesNotificationsEnabled = areUpdateNotificationsEnabled(),
            permissionGranted = hasNotificationPermission(),
            permissionPromptShown = settingsRepository.isNotificationPermissionPromptShown(user.uid),
            active = true,
            updatedAtMillis = now
        )

        val result = suspendCancellableCoroutine<Result<Unit>> { continuation ->
            userStatsService.uploadNotificationDevice(user.uid, payload) { success ->
                continuation.resume(
                    if (success) {
                        Result.success(Unit)
                    } else {
                        Result.failure(IllegalStateException("Failed to upload notification device"))
                    }
                )
            }
        }

        if (result.isSuccess) {
            settingsRepository.setCachedNotificationToken(token)
            settingsRepository.setNotificationBoundUserId(user.uid)
            settingsRepository.setNotificationLastSyncedAt(now)
        }

        return result
    }

    private suspend fun resolveCurrentToken(): String? {
        val cached = settingsRepository.getCachedNotificationToken()
        val remote = fetchFirebaseToken()
        val resolved = remote?.takeIf { it.isNotBlank() } ?: cached
        if (!remote.isNullOrBlank()) {
            settingsRepository.setCachedNotificationToken(remote)
        }
        return resolved
    }

    private suspend fun fetchFirebaseToken(): String? {
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!continuation.isActive) return@addOnCompleteListener
                    continuation.resume(
                        if (task.isSuccessful) task.result?.takeIf { it.isNotBlank() } else null
                    )
                }
        }
    }
}
