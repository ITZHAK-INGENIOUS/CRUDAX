package com.crudax.launcher.shizuku

import android.content.Context
import android.content.pm.PackageManager

/**
 * Optional Shizuku integration.
 * We never depend on the Shizuku library at compile time for the MVP
 * so the project compiles without the AAR. Detection is package-based.
 * Full binder usage can be added later behind this interface.
 */
class DefaultShizukuManager(private val context: Context) : ShizukuManager {

    companion object {
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    }

    override fun isInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun isAvailable(): Boolean = isInstalled()

    override fun getDiagnostic(): ShizukuDiagnostic {
        val installed = isInstalled()
        return ShizukuDiagnostic(
            installed = installed,
            available = installed,
            binderAlive = false // requires Shizuku API library
        )
    }
}
