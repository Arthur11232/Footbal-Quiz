package com.arthuralexandryan.footballquiz.models

data class UserNotificationDeviceDTO(
    var installationId: String = "",
    var fcmToken: String = "",
    var platform: String = "android",
    var appVersionName: String = "",
    var appVersionCode: Long = 0L,
    var locale: String = "",
    var userEmail: String = "",
    var notificationsEnabled: Boolean = true,
    var progressNotificationsEnabled: Boolean = true,
    var rewardNotificationsEnabled: Boolean = true,
    var updatesNotificationsEnabled: Boolean = true,
    var permissionGranted: Boolean = false,
    var permissionPromptShown: Boolean = false,
    var active: Boolean = true,
    var updatedAtMillis: Long = 0L
)
