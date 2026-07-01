package com.arthuralexandryan.footballquiz.presentation.profile

import com.arthuralexandryan.footballquiz.presentation.progression.CompletedStageUiModel

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val isSignedIn: Boolean = false,
    val showNotificationSettings: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val progressNotificationsEnabled: Boolean = true,
    val rewardNotificationsEnabled: Boolean = true,
    val updateNotificationsEnabled: Boolean = true,
    val notificationPermissionGranted: Boolean = false,
    val localPhotoPath: String? = null,
    val remotePhotoUrl: String? = null,
    val totalScoreText: String = "0%",
    val answeredCountText: String = "0/0",
    val top5ProgressText: String = "0/0",
    val uefaProgressText: String = "0/0",
    val worldProgressText: String = "0/0",
    val versusProgressText: String = "0/0",
    val certificates: List<ProfileCertificateUiModel> = emptyList(),
    val certificatesCount: Int = 0,
    val completedStages: List<CompletedStageUiModel> = emptyList(),
    val completedStagesCount: Int = 0,
    val totalStagesCount: Int = 0
)
