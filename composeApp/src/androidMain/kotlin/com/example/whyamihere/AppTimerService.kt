package com.example.whyamihere

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AppTimerService : Service() {

    private var timer: CountDownTimer? = null
    private val channelId = "timer_channel"
    private val notificationId = 101

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val totalTime = intent?.getLongExtra("TIME", 0L) ?: 0L

        timer?.cancel()

        timer = object : CountDownTimer(totalTime, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val min = seconds / 60
                val sec = seconds % 60

                val timeText = String.format("%02d:%02d", min, sec)

                val notification = NotificationCompat.Builder(this@AppTimerService, channelId)
                    .setContentTitle("App Timer Running")
                    .setContentText("Time left: $timeText")
                    .setSmallIcon(R.drawable.ic_lock_idle_alarm)
                    .setOngoing(true)
                    .build()

                startForeground(notificationId, notification)
            }

            override fun onFinish() {
                stopForeground(true)

                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(homeIntent)

                stopSelf()
            }
        }.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}