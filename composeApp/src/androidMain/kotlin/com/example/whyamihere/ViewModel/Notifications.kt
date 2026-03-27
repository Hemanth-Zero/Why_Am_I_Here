package com.example.whyamihere.ViewModel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.whyamihere.BreakTimerService
import com.example.whyamihere.MainActivity

private const val CHANNEL_APP_MONITOR  = "APP_CHANNEL"
private const val CHANNEL_BREAK        = "BREAK_CHANNEL"
const val CHANNEL_BREAK_TIMER          = "BREAK_TIMER_CHANNEL"
const val NOTIF_ID_BREAK_TIMER         = 42

fun createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_APP_MONITOR, "App Monitor",
                NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifies when tracked apps open"
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_BREAK, "Break Reminders",
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Gentle reminders to take a break"
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_BREAK_TIMER, "Break Countdown",
                NotificationManager.IMPORTANCE_LOW).apply {
                description = "Counts down your chosen break time"
            }
        )
    }
}

fun sendNotification(context: Context, text: String) {
    Log.d("APP_MONITOR", "Sending notification: $text")
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    nm.notify(1, NotificationCompat.Builder(context, CHANNEL_APP_MONITOR)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("App Monitor")
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .build())
}

fun sendBreakNotification(context: Context, appName: String) {
    Log.d("APP_MONITOR", "Break reminder for $appName")
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    nm.notify(2, NotificationCompat.Builder(context, CHANNEL_BREAK)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Time for a break! ☕")
        .setContentText("You've been on $appName for a while. Consider taking a short break.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build())
}

/**
 * Posts/updates the persistent countdown notification shown while the break timer runs.
 */
fun postBreakTimerNotification(context: Context, remainingSecs: Int) {
    val mins = remainingSecs / 60
    val secs = remainingSecs % 60
    val timeStr = String.format("%d:%02d", mins, secs)

    val tapIntent = PendingIntent.getActivity(
        context, 0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val cancelIntent = Intent(context, BreakTimerService::class.java).apply {
        action = BreakTimerService.ACTION_CANCEL
    }

    val cancelPendingIntent = PendingIntent.getService(
        context,
        1,
        cancelIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    nm.notify(
        NOTIF_ID_BREAK_TIMER,
        NotificationCompat.Builder(context, CHANNEL_BREAK_TIMER)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("Break time ⏳")
            .setContentText("Overlay returns in $timeStr")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // can't be swiped away
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Cancel",
                cancelPendingIntent   // 🔥 BUTTON
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(tapIntent)
            .build()
    )
}


fun cancelBreakTimerNotification(context: Context) {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    nm.cancel(NOTIF_ID_BREAK_TIMER)
}
