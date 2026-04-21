package com.arthuralexandryan.footballquiz.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import com.arthuralexandryan.footballquiz.R

@SuppressLint("MissingPermission", "NotificationPermission")
fun showNotification(context: Context, title: String, message: String){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = "channel-01"
    val notificationId = 1
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    val resultPendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
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
