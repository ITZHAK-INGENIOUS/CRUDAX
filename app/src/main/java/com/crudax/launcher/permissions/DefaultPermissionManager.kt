package com.crudax.launcher.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

class DefaultPermissionManager(private val context: Context) : PermissionManager {

    override fun getAll(): List<PermissionInfo> {
        return listOf(
            PermissionInfo(
                name = "INTERNET",
                rationale = "Documentation, liens externes, Termux réseau optionnel",
                status = PermissionStatus.GRANTED, // normal permission
                feature = "Réseau / docs"
            ),
            PermissionInfo(
                name = "POST_NOTIFICATIONS",
                rationale = "Fin d'automatisation, erreurs, tâches longues",
                status = if (isNotificationGranted()) PermissionStatus.GRANTED else PermissionStatus.DENIED,
                feature = "Notifications"
            ),
            PermissionInfo(
                name = "SYSTEM_ALERT_WINDOW",
                rationale = "Contrôles flottants uniquement si l'utilisateur les active",
                status = if (isOverlayGranted()) PermissionStatus.GRANTED else PermissionStatus.DENIED,
                feature = "Overlay"
            ),
            PermissionInfo(
                name = "RECEIVE_BOOT_COMPLETED",
                rationale = "Automatisations planifiées au démarrage (désactivé par défaut)",
                status = PermissionStatus.NOT_NEEDED,
                feature = "Boot automations"
            )
        )
    }

    override fun isNotificationGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    override fun isOverlayGranted(): Boolean = Settings.canDrawOverlays(context)

    override fun canDrawOverlays(): Boolean = Settings.canDrawOverlays(context)
}
