package com.example.whyamihere

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.*
import com.example.compose.AppTheme
import com.example.whyamihere.AppTimerService
import com.example.whyamihere.Model.IntentionStore
import com.example.whyamihere.View.IntentionOverlay
import kotlin.jvm.java

class OverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private var windowManager: WindowManager? = null
    private var composeView: ComposeView? = null

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_APP_NAME = "app_name"

        private var isOverlayShowing = false

        fun show(context: Context, packageName: String, appName: String) {
            if (isOverlayShowing) return

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
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: packageName

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

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)

            setContent {
                AppTheme(darkTheme = true, dynamicColor = false) {
                    IntentionOverlay(
                        appName = appName,
                        packageName = packageName,

                        // 🔥 FIXED LOGIC HERE
                        onIntentionSet = { intention ->

                            // ✅ Handle timed session
                            if (intention == "timed_session") {
                                val timerIntent = Intent(
                                    this@OverlayService,
                                    AppTimerService::class.java
                                )
                                timerIntent.putExtra("TIME", 10 * 60 * 1000L) // 10 mins
                                startService(timerIntent)
                            }

                            IntentionStore.setIntention(
                                this@OverlayService,
                                packageName,
                                intention
                            )

                            dismiss(this@OverlayService)
                        },

                        onExit = {
                            IntentionStore.clearIntention(this@OverlayService, packageName)
                            dismiss(this@OverlayService)

                            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(homeIntent)
                        }
                    )
                }
            }
        }

        windowManager?.addView(composeView, params)
        isOverlayShowing = true
    }

    override fun onDestroy() {
        composeView?.let {
            try { windowManager?.removeView(it) } catch (_: Exception) {}
        }
        composeView = null
        isOverlayShowing = false
        super.onDestroy()
    }
}