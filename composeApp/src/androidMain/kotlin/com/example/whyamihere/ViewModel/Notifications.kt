package com.example.whyamihere.ViewModel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat

fun createChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val channel = NotificationChannel(
            "APP_CHANNEL",        // channel ID
            "App Monitor",        // channel name
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.description = "Notifies when apps open"

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

fun sendNotification(context: Context, text: String) {
    Log.d("APP_MONITOR", "Opened notifi")
    val notification = NotificationCompat.Builder(context, "APP_CHANNEL")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("App Monitor")
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(1, notification)
}