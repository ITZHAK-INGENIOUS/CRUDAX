package com.crudax.launcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.crudax.launcher.core.CrudaxContainer
import com.crudax.launcher.storage.CrudaxDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CrudaxApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var container: CrudaxContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = CrudaxContainer(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_AUTOMATION,
                "Automatisations",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Fin d'automatisation, erreurs, tâches longues"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_AUTOMATION = "crudax_automation"
        lateinit var instance: CrudaxApp
            private set
    }
}
