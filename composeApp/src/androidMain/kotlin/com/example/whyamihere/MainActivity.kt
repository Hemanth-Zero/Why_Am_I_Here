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
import com.example.compose.AppTheme
import com.example.whyamihere.View.AppNavi
import com.example.whyamihere.ViewModel.MyAppViewModel
import com.example.whyamihere.ViewModel.createChannel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        createChannel(this)
        window.decorView.setBackgroundColor(android.graphics.Color.parseColor("#121212"))
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Request overlay permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        val myAppViewModel = MyAppViewModel(this)
        setContent {
            AppTheme(darkTheme = true, dynamicColor = false) {
                AppNavi(myAppViewModel)
            }
        }
    }
}
