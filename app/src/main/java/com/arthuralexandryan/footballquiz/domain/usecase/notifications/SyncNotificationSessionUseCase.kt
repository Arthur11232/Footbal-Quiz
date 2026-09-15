package com.arthuralexandryan.footballquiz.domain.usecase.notifications

import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser

class SyncNotificationSessionUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(user: AuthenticatedUser): Result<Unit> =
        notificationRepository.syncSignedInUser(user)
}
