package com.example.whyamihere

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.compose.AppTheme
import com.example.whyamihere.Model.TrackedAppsPrefs
import com.example.whyamihere.View.IntentionOverlay
import com.example.whyamihere.View.OverlayCallbacks

class OverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private var windowManager: WindowManager? = null
    private var composeView: ComposeView? = null

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_APP_NAME     = "app_name"

        @Volatile private var isOverlayShowing = false

        fun show(context: Context, packageName: String, appName: String) {
            if (isOverlayShowing) {
                Log.d("OverlayService", "Overlay already showing, skipping")
                return
            }
            val intent = Intent(context, OverlayService::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_APP_NAME, appName)
            }
            context.startService(intent)
        }

        fun dismiss(context: Context) {
            isOverlayShowing = false
            context.stopService(Intent(context, OverlayService::class.java))
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val packageName = intent?.getStringExtra(EXTRA_PACKAGE_NAME) ?: return START_NOT_STICKY
        val appName     = intent.getStringExtra(EXTRA_APP_NAME) ?: packageName
        showOverlay(packageName, appName)
        return START_NOT_STICKY
    }

    private fun showOverlay(packageName: String, appName: String) {
        if (composeView != null) return

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        val ctx = this

        val callbacks = OverlayCallbacks(
            // ── Button 1: Unlimited – dismiss overlay for the rest of today ──
            onUnlimited = {
                TrackedAppsPrefs.setUnlimited(ctx, packageName, true)
                dismiss(ctx)
            },

            // ── Button 2: Break interval defined in Settings ─────────────────
            onBreakInterval = {
                val breakMins = TrackedAppsPrefs.getBreakReminderMinutes(ctx)
                dismiss(ctx)
                BreakTimerService.start(ctx, breakMins * 60, packageName, appName)
            },

            // ── Button 3: User-chosen custom timer (5-30 min) ────────────────
            onCustomTimer = { minutes ->
                dismiss(ctx)
                BreakTimerService.start(ctx, minutes * 60, packageName, appName)
            },

            // ── Button 4: Exit – go home ──────────────────────────────────────
            onExit = {
                dismiss(ctx)
                startActivity(Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        )

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            setContent {
                AppTheme(darkTheme = true, dynamicColor = false) {
                    IntentionOverlay(
                        appName     = appName,
                        packageName = packageName,
                        callbacks   = callbacks
                    )
                }
            }
        }

        try {
            windowManager?.addView(composeView, params)
            isOverlayShowing = true
        } catch (e: Exception) {
            Log.e("OverlayService", "Error adding overlay: ${e.message}")
        }
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        composeView?.let { try { windowManager?.removeView(it) } catch (_: Exception) {} }
        composeView = null
        isOverlayShowing = false
        super.onDestroy()
    }
}
