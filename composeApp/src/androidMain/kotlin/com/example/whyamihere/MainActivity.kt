package com.example.whyamihere

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import com.example.compose.AppTheme
import com.example.whyamihere.View.AppNavi
import com.example.whyamihere.ViewModel.MyAppViewModel
import com.example.whyamihere.ViewModel.cancelBreakTimerNotification
import com.example.whyamihere.ViewModel.createChannel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        createChannel(this)
        window.decorView.setBackgroundColor(android.graphics.Color.parseColor("#121212"))
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            )
        }

        val myAppViewModel = MyAppViewModel(this)
        setContent {
            // Reactively read theme state so recomposition occurs when user changes it
            val themeMode    by androidx.compose.runtime.remember { androidx.compose.runtime.derivedStateOf { myAppViewModel.themeMode } }
            val dynamicColor by androidx.compose.runtime.remember { androidx.compose.runtime.derivedStateOf { myAppViewModel.dynamicColor } }
            val systemDark   = isSystemInDarkTheme()

            val isDark = when (myAppViewModel.themeMode) {
                "light"  -> false
                "dark"   -> true
                else     -> systemDark
            }

            AppTheme(darkTheme = isDark, dynamicColor = myAppViewModel.dynamicColor) {
                AppNavi(myAppViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelBreakTimerNotification(this)
        stopService(Intent(this, BreakTimerService::class.java))
    }
}
