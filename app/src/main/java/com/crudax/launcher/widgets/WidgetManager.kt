package com.crudax.launcher.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetProviderInfo
import android.os.Bundle

data class WidgetItem(
    val widgetId: Int,
    val provider: String,
    val x: Int = 0,
    val y: Int = 0,
    val width: Int = 2,
    val height: Int = 1,
    val page: Int = 0,
    val options: Bundle? = null
)

interface WidgetManager {
    fun getHost(): AppWidgetHost
    fun allocateId(): Int
    fun deleteId(id: Int)
    fun getInstalledProviders(): List<AppWidgetProviderInfo>
    fun bindWidget(widgetId: Int, provider: AppWidgetProviderInfo): Boolean
    suspend fun saveWidgets(items: List<WidgetItem>)
    suspend fun loadWidgets(): List<WidgetItem>
}
