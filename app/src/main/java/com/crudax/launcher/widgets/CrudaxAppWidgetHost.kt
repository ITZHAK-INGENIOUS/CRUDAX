package com.crudax.launcher.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Placeholder. Real widget hosting is done via AppWidgetHost in DefaultWidgetManager.
 * This receiver exists only to keep the manifest clean; it does nothing.
 */
class CrudaxAppWidgetHost : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // no-op
    }
}
