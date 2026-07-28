package com.arthuralexandryan.footballquiz.domain.usecase.notifications

import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository

class MarkNotificationPermissionPromptShownUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String) {
        notificationRepository.markPermissionPromptShown(userId)
    }
}
