package com.arthuralexandryan.footballquiz.domain.usecase.notifications

import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository

class UnbindNotificationSessionUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(userId: String?): Result<Unit> =
        notificationRepository.unbindCurrentDevice(userId)
}
