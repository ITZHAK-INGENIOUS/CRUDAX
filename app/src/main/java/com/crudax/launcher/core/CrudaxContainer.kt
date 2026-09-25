package com.crudax.launcher.core

import android.content.Context
import com.crudax.launcher.apps.AppManager
import com.crudax.launcher.apps.DefaultAppManager
import com.crudax.launcher.automation.AutomationManager
import com.crudax.launcher.automation.DefaultAutomationManager
import com.crudax.launcher.files.DefaultFileManager
import com.crudax.launcher.files.FileManager
import com.crudax.launcher.permissions.DefaultPermissionManager
import com.crudax.launcher.permissions.PermissionManager
import com.crudax.launcher.shizuku.DefaultShizukuManager
import com.crudax.launcher.shizuku.ShizukuManager
import com.crudax.launcher.storage.CrudaxDatabase
import com.crudax.launcher.storage.PreferencesRepository
import com.crudax.launcher.termux.DefaultTermuxManager
import com.crudax.launcher.termux.TermuxManager
import com.crudax.launcher.widgets.DefaultWidgetManager
import com.crudax.launcher.widgets.WidgetManager

/**
 * Simple service locator / DI container.
 * Keeps all integrations behind interfaces so missing Termux/Shizuku never crash the app.
 */
class CrudaxContainer(context: Context) {

    private val appContext = context.applicationContext

    val database: CrudaxDatabase by lazy {
        CrudaxDatabase.getInstance(appContext)
    }

    val preferences: PreferencesRepository by lazy {
        PreferencesRepository(appContext)
    }

    val permissionManager: PermissionManager by lazy {
        DefaultPermissionManager(appContext)
    }

    val termuxManager: TermuxManager by lazy {
        DefaultTermuxManager(appContext)
    }

    val shizukuManager: ShizukuManager by lazy {
        DefaultShizukuManager(appContext)
    }

    val appManager: AppManager by lazy {
        DefaultAppManager(appContext)
    }

    val fileManager: FileManager by lazy {
        DefaultFileManager(appContext)
    }

    val widgetManager: WidgetManager by lazy {
        DefaultWidgetManager(appContext)
    }

    val automationManager: AutomationManager by lazy {
        DefaultAutomationManager(
            appContext,
            database,
            termuxManager,
            fileManager,
            appManager
        )
    }
}
