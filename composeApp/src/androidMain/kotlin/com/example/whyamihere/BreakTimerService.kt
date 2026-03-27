package com.example.whyamihere

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.whyamihere.ViewModel.cancelBreakTimerNotification
import com.example.whyamihere.ViewModel.createChannel
import com.example.whyamihere.ViewModel.postBreakTimerNotification
import kotlinx.coroutines.*

class BreakTimerService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null

    private var currentPackage: String = ""

    companion object {
        const val EXTRA_DURATION_SECS = "duration_secs"
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_APP_NAME = "app_name"
        const val ACTION_CANCEL = "action_cancel_timer"

        // 🔥 PER-APP tracking
        val activeTimers = mutableSetOf<String>()

        fun start(context: Context, durationSecs: Int, packageName: String, appName: String) {
            val intent = Intent(context, BreakTimerService::class.java).apply {
                putExtra(EXTRA_DURATION_SECS, durationSecs)
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_APP_NAME, appName)
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createChannel(this)

        startForeground(
            com.example.whyamihere.ViewModel.NOTIF_ID_BREAK_TIMER,
            buildInitialNotification()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildInitialNotification() =
        android.app.Notification.Builder(
            this,
            com.example.whyamihere.ViewModel.CHANNEL_BREAK_TIMER
        )
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("Break time ⏳")
            .setContentText("Starting countdown…")
            .build()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        if (intent?.action == ACTION_CANCEL) {
            Log.d("BreakTimerService", "Timer cancelled for $currentPackage")
            activeTimers.remove(currentPackage)
            cancelBreakTimerNotification(this)
            stopSelf()
            return START_NOT_STICKY
        }

        val durationSecs = intent?.getIntExtra(EXTRA_DURATION_SECS, 300) ?: 300
        val packageName = intent?.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val appName = intent?.getStringExtra(EXTRA_APP_NAME) ?: packageName

        if (packageName.isEmpty()) return START_NOT_STICKY

        currentPackage = packageName


        activeTimers.add(packageName)

        timerJob?.cancel()
        timerJob = scope.launch {
            var remaining = durationSecs

            while (remaining > 0 && isActive) {
                postBreakTimerNotification(this@BreakTimerService, remaining)
                delay(1000)
                remaining--
            }

            if (isActive) {
                Log.d("BreakTimerService", "Timer done → $packageName")

                activeTimers.remove(packageName)
                cancelBreakTimerNotification(this@BreakTimerService)

                val currentApp = getForegroundApp(this@BreakTimerService)

                if (currentApp == packageName) {
                    OverlayService.show(this@BreakTimerService, packageName, appName)
                } else {
                    Log.d("BreakTimerService", "Skipped overlay, user not in $packageName")
                }
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timerJob?.cancel()
        scope.cancel()

        // 🔥 CLEANUP (VERY IMPORTANT)
        activeTimers.remove(currentPackage)
        cancelBreakTimerNotification(this)

        super.onDestroy()
    }
}

fun getForegroundApp(context: Context): String? {
    val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager

    val time = System.currentTimeMillis()
    val stats = usm.queryUsageStats(
        android.app.usage.UsageStatsManager.INTERVAL_DAILY,
        time - 10_000,
        time
    )

    if (stats.isNullOrEmpty()) return null

    val recent = stats.maxByOrNull { it.lastTimeUsed }
    return recent?.packageName
}