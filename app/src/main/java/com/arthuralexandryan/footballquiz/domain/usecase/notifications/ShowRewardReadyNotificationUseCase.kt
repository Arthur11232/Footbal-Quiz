package com.arthuralexandryan.footballquiz.domain.usecase.notifications

import android.content.Context
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository
import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload
import com.arthuralexandryan.footballquiz.utils.showNotification

class ShowRewardReadyNotificationUseCase(
    private val context: Context,
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(payload: AchievementCertificatePayload) {
        if (!notificationRepository.areNotificationsEnabled()) return
        if (!notificationRepository.areRewardNotificationsEnabled()) return
        if (!notificationRepository.hasNotificationPermission()) return
        if (notificationRepository.getBoundUserId().isNullOrBlank()) return

        val totalCertificates = 1 + payload.additionalStageTitleResIds.size
        val title = context.getString(R.string.reward_notification_title)
        val body = if (totalCertificates == 1) {
            val stageTitle = context.getString(payload.primaryStageTitleResId)
            context.getString(R.string.reward_notification_body_single, stageTitle)
        } else {
            context.getString(R.string.reward_notification_body_multiple, totalCertificates)
        }

        showNotification(
            context = context,
            title = title,
            message = body
        )
    }
}
