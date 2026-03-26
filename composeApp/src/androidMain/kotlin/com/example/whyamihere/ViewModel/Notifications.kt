package com.example.whyamihere.ViewModel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat

private const val CHANNEL_APP_MONITOR = "APP_CHANNEL"
private const val CHANNEL_BREAK = "BREAK_CHANNEL"

fun createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // App monitor channel
        val appChannel = NotificationChannel(
            CHANNEL_APP_MONITOR,
            "App Monitor",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Notifies when tracked apps open" }
        manager.createNotificationChannel(appChannel)

        // Break reminder channel
        val breakChannel = NotificationChannel(
            CHANNEL_BREAK,
            "Break Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Gentle reminders to take a break" }
        manager.createNotificationChannel(breakChannel)
    }
}

fun sendNotification(context: Context, text: String) {
    Log.d("APP_MONITOR", "Sending notification: $text")
    val notification = NotificationCompat.Builder(context, CHANNEL_APP_MONITOR)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("App Monitor")
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(1, notification)
}

fun sendBreakNotification(context: Context, appName: String) {
    Log.d("APP_MONITOR", "Break reminder for $appName")
    val notification = NotificationCompat.Builder(context, CHANNEL_BREAK)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Time for a break! ☕")
        .setContentText("You've been on $appName for a while. Consider taking a short break.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(2, notification)
}
