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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.activities.MainActivity

private const val DEFAULT_CHANNEL_ID = "football_quiz_general"

fun ensureDefaultNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelName = context.getString(R.string.notification_channel_name)
    val channel = NotificationChannel(
        DEFAULT_CHANNEL_ID,
        channelName,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = context.getString(R.string.notification_channel_description)
    }
    notificationManager.createNotificationChannel(channel)
}

@SuppressLint("MissingPermission", "NotificationPermission")
fun showNotification(context: Context, title: String, message: String){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = DEFAULT_CHANNEL_ID
    val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    ensureDefaultNotificationChannel(context)

    val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val resultPendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    mBuilder.setContentIntent(resultPendingIntent)
    mBuilder.setAutoCancel(true)
    mBuilder.setSound(soundUri)
    mBuilder.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)

    notificationManager.notify(notificationId, mBuilder.build())
}

fun getOpenedPlace(score: Int, context: Context) {
    when(score){
        150 -> showNotification(context, context.getString(R.string.not_cong_title), String.format(context.getString(R.string.not_cong_body), "UEFA"))
        210 -> showNotification(context, context.getString(R.string.not_cong_title), String.format(context.getString(R.string.not_cong_body), "World"))
        240 -> showNotification(context, context.getString(R.string.not_cong_title), String.format(context.getString(R.string.not_cong_body), "Versus"))
    }
}
