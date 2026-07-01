package com.arthuralexandryan.footballquiz.domain.usecase.notifications

import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository

class ShouldPromptNotificationPermissionUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String): Boolean =
        notificationRepository.shouldShowPermissionPrompt(userId)
}
