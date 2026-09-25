package com.crudax.launcher.diagnostics

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.crudax.launcher.BuildConfig
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.R

class DiagnosticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this).apply {
            setPadding(48, 48, 48, 48)
            textSize = 14f
            setTextIsSelectable(true)
        }
        setContentView(tv)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.diagnostics)

        val c = CrudaxApp.instance.container
        val termux = c.termuxManager.getDiagnostic()
        val shizuku = c.shizukuManager.getDiagnostic()
        val perms = c.permissionManager.getAll()

        val sb = StringBuilder()
        sb.appendLine("=== ANDROID ===")
        sb.appendLine("Version : ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
        sb.appendLine("Device  : ${Build.MANUFACTURER} ${Build.MODEL}")
        sb.appendLine("ABI     : ${Build.SUPPORTED_ABIS.joinToString()}")
        sb.appendLine()
        sb.appendLine("=== CRUDAX ===")
        sb.appendLine("Version : ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
        sb.appendLine("Build   : ${if (BuildConfig.DEBUG) "debug" else "release"}")
        sb.appendLine()
        sb.appendLine("=== TERMUX ===")
        sb.appendLine("Installé     : ${if (termux.installed) "✓" else "✗"}")
        sb.appendLine("Version      : ${termux.version ?: "—"}")
        sb.appendLine("RUN_COMMAND  : ${if (termux.runCommandAllowed) "✓ (best-effort)" else "✗"}")
        sb.appendLine("allow-ext    : ${termux.allowExternalApps?.let { if (it) "✓" else "✗" } ?: "inconnu"}")
        sb.appendLine("Termux:API   : ${if (termux.apiInstalled) "✓" else "✗"}")
        sb.appendLine("Termux:GUI   : ${if (termux.guiInstalled) "✓" else "✗"}")
        sb.appendLine()
        sb.appendLine("=== SHIZUKU ===")
        sb.appendLine("Installé     : ${if (shizuku.installed) "✓" else "✗"}")
        sb.appendLine("Disponible   : ${if (shizuku.available) "✓" else "✗"}")
        sb.appendLine()
        sb.appendLine("=== PERMISSIONS ===")
        perms.forEach { p ->
            val icon = when (p.status) {
                com.crudax.launcher.permissions.PermissionStatus.GRANTED -> "✓"
                com.crudax.launcher.permissions.PermissionStatus.DENIED -> "✗"
                com.crudax.launcher.permissions.PermissionStatus.NOT_NEEDED -> "○"
                else -> "?"
            }
            sb.appendLine("$icon ${p.name} — ${p.feature}")
        }
        sb.appendLine()
        sb.appendLine("=== NIVEAU ===")
        val level = when {
            shizuku.available && termux.guiInstalled -> 5
            termux.guiInstalled -> 4
            termux.apiInstalled -> 3
            termux.installed -> 2
            else -> 0
        }
        sb.appendLine("CRUDAX LEVEL $level")

        tv.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
