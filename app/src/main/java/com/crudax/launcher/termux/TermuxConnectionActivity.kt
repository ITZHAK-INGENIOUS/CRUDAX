package com.crudax.launcher.termux

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.R
import kotlinx.coroutines.launch

class TermuxConnectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this).apply {
            setPadding(48, 48, 48, 48)
            textSize = 15f
            setTextIsSelectable(true)
        }
        setContentView(tv)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.termux_connection)

        val d = CrudaxApp.instance.container.termuxManager.getDiagnostic()
        val sb = StringBuilder()
        sb.appendLine("TERMUX")
        sb.appendLine(if (d.installed) "✓ installé" else "✗ non installé")
        sb.appendLine()
        sb.appendLine("RUN_COMMAND")
        sb.appendLine(if (d.runCommandAllowed) "✓ autorisé (best-effort)" else "✗ non autorisé")
        sb.appendLine()
        sb.appendLine("allow-external-apps")
        sb.appendLine(d.allowExternalApps?.let { if (it) "✓ actif" else "✗ à configurer" } ?: "○ à vérifier dans Termux")
        sb.appendLine()
        sb.appendLine("Pour activer RUN_COMMAND :")
        sb.appendLine("1. Ouvrir Termux")
        sb.appendLine("2. nano ~/.termux/termux.properties")
        sb.appendLine("3. allow-external-apps=true")
        sb.appendLine("4. Redémarrer Termux")
        sb.appendLine()
        sb.appendLine("TERMUX:API  ${if (d.apiInstalled) "✓" else "✗"}")
        sb.appendLine("TERMUX:GUI  ${if (d.guiInstalled) "✓" else "✗"}")
        sb.appendLine()
        sb.appendLine("Test commande :")
        tv.text = sb.toString()

        // Optional test button could be added
        lifecycleScope.launch {
            if (d.installed) {
                // no auto-run
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
