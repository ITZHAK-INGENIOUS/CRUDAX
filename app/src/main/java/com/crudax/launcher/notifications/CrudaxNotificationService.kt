package com.crudax.launcher.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Optional foreground service for long-running automations.
 * Not started automatically.
 */
class CrudaxNotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Future: startForeground with automation status
        return START_NOT_STICKY
    }
}
