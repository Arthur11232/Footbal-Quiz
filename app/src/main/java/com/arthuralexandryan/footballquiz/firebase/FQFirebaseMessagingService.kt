package com.arthuralexandryan.footballquiz.firebase

import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.data.repository.NotificationRepository
import com.arthuralexandryan.footballquiz.utils.showNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FQFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationRepository: NotificationRepository by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            notificationRepository.handleNewToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (!notificationRepository.areNotificationsEnabled()) return

        when (remoteMessage.data["type"]) {
            "progress" -> if (!notificationRepository.areProgressNotificationsEnabled()) return
            "reward" -> if (!notificationRepository.areRewardNotificationsEnabled()) return
            "update" -> if (!notificationRepository.areUpdateNotificationsEnabled()) return
        }

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: getString(R.string.app_name)
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: return

        showNotification(
            context = applicationContext,
            title = title,
            message = body
        )
    }
}
