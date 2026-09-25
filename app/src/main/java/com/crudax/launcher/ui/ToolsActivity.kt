package com.crudax.launcher.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.R
import com.crudax.launcher.termux.TermuxConnectionActivity

class ToolsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.tools)

        val termux = CrudaxApp.instance.container.termuxManager
        val status = findViewById<TextView>(R.id.termuxStatus)
        if (termux.isInstalled()) {
            status.text = "Termux détecté\nOuvrir • Scripts • Paquets • Diagnostic"
            findViewById<TextView>(R.id.btnOpenTermux).setOnClickListener { termux.openTermux() }
            findViewById<TextView>(R.id.btnTermuxDiag).setOnClickListener {
                startActivity(Intent(this, TermuxConnectionActivity::class.java))
            }
        } else {
            status.text = getString(R.string.termux_not_detected)
            findViewById<TextView>(R.id.btnOpenTermux).text = getString(R.string.install_termux)
            findViewById<TextView>(R.id.btnOpenTermux).setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://github.com/termux/termux-app"))
                startActivity(i)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
