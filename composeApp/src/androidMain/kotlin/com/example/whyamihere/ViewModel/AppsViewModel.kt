package com.example.whyamihere.ViewModel

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.whyamihere.Model.AppUsage
import com.example.whyamihere.Model.UsageStatsRepository

class MyAppViewModel( private val context: Context): ViewModel(){
    private val usageRepository = UsageStatsRepository(context)

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getUsageList():List<AppUsage> {
        return usageRepository.getTodayUsage()
    }
    val result = mutableListOf<AppData>()
    fun getNonSystemApps(): List<AppData> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        apps.forEach { app ->
            if ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                result.add(
                    AppData(
                        name = pm.getApplicationLabel(app).toString(),
                        icon = pm.getApplicationIcon(app),
                        packageName = app.packageName,
                        onTrack = false
                    )
                )
            }
        }
        return result
    }
}

data class AppData(
    val name : String,
    val icon : Drawable,
    val packageName : String,
    val onTrack : Boolean
)