package com.arthuralexandryan.footballquiz.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.arthuralexandryan.footballquiz.R

fun showNotification(context: Context, title: String, message: String){
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = "channel-01"
    val notificationId = 1
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(
                channelId, channelName, importance)
        notificationManager.createNotificationChannel(mChannel)
    }

    val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
    val intent = Intent()

    val resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    mBuilder.setContentIntent(resultPendingIntent)
    mBuilder.setAutoCancel(true)
    mBuilder.setSound(soundUri)
    mBuilder.setDefaults(Notification.DEFAULT_LIGHTS and Notification.DEFAULT_VIBRATE)

    notificationManager.notify(notificationId, mBuilder.build())
}

fun getOpenedPlace(score: Int, context: Context) {
    when(score){
        150 -> showNotification(context, context.getString(R.string.not_cong_title), String.format(context.getString(R.string.not_cong_body), "UEFA"))
        210 -> showNotification(context, context.getString(R.string.not_cong_title), String.format(context.getString(R.string.not_cong_body), "World"))
        240 -> showNotification(context, context.getString(R.string.not_cong_title), String.format(context.getString(R.string.not_cong_body), "Versus"))
    }
}