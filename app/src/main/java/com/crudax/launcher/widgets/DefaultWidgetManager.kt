package com.crudax.launcher.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultWidgetManager(private val context: Context) : WidgetManager {

    companion object {
        private const val HOST_ID = 0x43525544 // "CRUD"
    }

    private val host: AppWidgetHost = AppWidgetHost(context, HOST_ID)
    private val appWidgetManager = AppWidgetManager.getInstance(context)

    init {
        try {
            host.startListening()
        } catch (e: Exception) {
            Log.w("WidgetManager", "startListening failed", e)
        }
    }

    override fun getHost(): AppWidgetHost = host

    override fun allocateId(): Int = host.allocateAppWidgetId()

    override fun deleteId(id: Int) {
        try {
            host.deleteAppWidgetId(id)
        } catch (e: Exception) {
            Log.w("WidgetManager", "deleteId $id", e)
        }
    }

    override fun getInstalledProviders(): List<AppWidgetProviderInfo> {
        return try {
            appWidgetManager.installedProviders
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun bindWidget(widgetId: Int, provider: AppWidgetProviderInfo): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, provider.provider)
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("WidgetManager", "bind failed", e)
            false
        }
    }

    // Persistence simplified: in-memory for MVP; Room can be wired later
    private val persisted = mutableListOf<WidgetItem>()

    override suspend fun saveWidgets(items: List<WidgetItem>) {
        withContext(Dispatchers.IO) {
            persisted.clear()
            persisted.addAll(items)
        }
    }

    override suspend fun loadWidgets(): List<WidgetItem> = withContext(Dispatchers.IO) {
        persisted.toList()
    }
}
