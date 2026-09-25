package com.crudax.launcher.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

class DefaultAppManager(private val context: Context) : AppManager {

    override fun getLaunchableApps(): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        return resolveInfos.mapNotNull { ri ->
            try {
                val ai = ri.activityInfo.applicationInfo
                AppInfo(
                    label = ri.loadLabel(pm).toString(),
                    packageName = ai.packageName,
                    icon = ri.loadIcon(pm),
                    isSystem = (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            } catch (e: Exception) {
                Log.w("AppManager", "skip ${ri.activityInfo.packageName}", e)
                null
            }
        }.sortedBy { it.label.lowercase() }
    }

    override fun launchApp(packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else false
        } catch (e: Exception) {
            Log.e("AppManager", "launch failed $packageName", e)
            false
        }
    }

    override fun isInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            false
        }
    }
}
