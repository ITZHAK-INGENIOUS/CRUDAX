package com.crudax.launcher.automation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "BOOT_COMPLETED received (automations on boot not enabled by default)")
            // Future: run ON_BOOT automations if user enabled them
        }
    }
}
