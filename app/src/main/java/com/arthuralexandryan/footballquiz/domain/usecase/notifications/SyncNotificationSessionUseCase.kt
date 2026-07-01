package com.arthuralexandryan.footballquiz.domain.usecase.notifications

import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseUser

class SyncNotificationSessionUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(user: FirebaseUser): Result<Unit> =
        notificationRepository.syncSignedInUser(user)
}
