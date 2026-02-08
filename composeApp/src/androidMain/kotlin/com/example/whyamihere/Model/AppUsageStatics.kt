package com.example.whyamihere.Model


import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings

class UsagePermissionManager(private val context: Context) {
    fun hasAcess(): Boolean{
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    fun requestUsageAccess() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}