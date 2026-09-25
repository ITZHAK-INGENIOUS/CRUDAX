package com.crudax.launcher.apps

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable?,
    val isSystem: Boolean,
    val isFavorite: Boolean = false
)

interface AppManager {
    fun getLaunchableApps(): List<AppInfo>
    fun launchApp(packageName: String): Boolean
    fun isInstalled(packageName: String): Boolean
}
