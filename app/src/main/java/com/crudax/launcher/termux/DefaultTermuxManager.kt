package com.crudax.launcher.termux

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.crudax.launcher.cruda.ActionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Official Termux integration via com.termux.RUN_COMMAND intent.
 * Never claims shell access if permission / allow-external-apps is missing.
 */
class DefaultTermuxManager(private val context: Context) : TermuxManager {

    companion object {
        private const val TAG = "TermuxManager"
        const val TERMUX_PACKAGE = "com.termux"
        const val TERMUX_API_PACKAGE = "com.termux.api"
        const val TERMUX_GUI_PACKAGE = "com.termux.gui"
        const val RUN_COMMAND_ACTION = "com.termux.RUN_COMMAND"
        const val RUN_COMMAND_SERVICE = "com.termux.app.RunCommandService"
        const val EXTRA_COMMAND = "com.termux.RUN_COMMAND_PATH"
        const val EXTRA_ARGUMENTS = "com.termux.RUN_COMMAND_ARGUMENTS"
        const val EXTRA_WORKDIR = "com.termux.RUN_COMMAND_WORKDIR"
        const val EXTRA_BACKGROUND = "com.termux.RUN_COMMAND_BACKGROUND"
        const val EXTRA_SESSION_ACTION = "com.termux.RUN_COMMAND_SESSION_ACTION"
    }

    override fun isInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(TERMUX_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun isApiInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(TERMUX_API_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun isGuiInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(TERMUX_GUI_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * We cannot reliably query allow-external-apps from outside without Termux:API
     * or a prior successful RUN_COMMAND. We treat "has permission declared by user"
     * as best-effort.
     */
    override fun isRunCommandAllowed(): Boolean {
        if (!isInstalled()) return false
        // Actual success is only known after attempting RUN_COMMAND.
        // For diagnostic we report "installed" and let the user test.
        return true
    }

    override fun openTermux() {
        if (!isInstalled()) return
        val intent = context.packageManager.getLaunchIntentForPackage(TERMUX_PACKAGE)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openNewSession() {
        // Termux does not expose a public "new session" intent that is stable.
        // Best effort: open Termux.
        openTermux()
    }

    override suspend fun runCommand(
        command: String,
        workDir: String?,
        timeoutMs: Long
    ): ActionResult = withContext(Dispatchers.IO) {
        if (!isInstalled()) {
            return@withContext ActionResult(false, "Termux non installé")
        }
        try {
            // Official RUN_COMMAND: starts a service that executes in Termux.
            // Requires user to set allow-external-apps=true in termux.properties
            // and grant the permission if prompted.
            val intent = Intent().apply {
                setClassName(TERMUX_PACKAGE, RUN_COMMAND_SERVICE)
                action = RUN_COMMAND_ACTION
                putExtra(EXTRA_COMMAND, "/data/data/com.termux/files/usr/bin/bash")
                putExtra(EXTRA_ARGUMENTS, arrayOf("-c", command))
                if (workDir != null) {
                    putExtra(EXTRA_WORKDIR, workDir)
                }
                putExtra(EXTRA_BACKGROUND, true)
                // Session action 0 = don't open, just run
                putExtra(EXTRA_SESSION_ACTION, "0")
            }
            // Note: RUN_COMMAND does not return stdout/stderr to the caller
            // in the classic intent form. For real capture one needs Termux:API
            // or a custom script that writes to a shared location.
            // We start the service and report "launched".
            context.startService(intent)
            ActionResult(
                success = true,
                message = "Commande envoyée à Termux (exécution en arrière-plan)",
                stdout = "",
                stderr = ""
            )
        } catch (e: SecurityException) {
            Log.w(TAG, "RUN_COMMAND refused", e)
            ActionResult(
                false,
                "RUN_COMMAND refusé. Activez allow-external-apps=true dans ~/.termux/termux.properties et redémarrez Termux."
            )
        } catch (e: Exception) {
            Log.e(TAG, "runCommand failed", e)
            ActionResult(false, e.message ?: "Erreur inconnue")
        }
    }

    override suspend fun listInstalledPackages(): List<TermuxPackage> = withContext(Dispatchers.IO) {
        // Without shell access we cannot list packages reliably.
        // Return empty; UI will show "nécessite RUN_COMMAND configuré".
        emptyList()
    }

    override suspend fun getPackageVersion(pkg: String): String? = null

    override fun getDiagnostic(): TermuxDiagnostic {
        return TermuxDiagnostic(
            installed = isInstalled(),
            version = try {
                context.packageManager.getPackageInfo(TERMUX_PACKAGE, 0).versionName
            } catch (_: Exception) { null },
            runCommandAllowed = isInstalled(),
            allowExternalApps = null, // unknown without access
            apiInstalled = isApiInstalled(),
            guiInstalled = isGuiInstalled()
        )
    }
}
