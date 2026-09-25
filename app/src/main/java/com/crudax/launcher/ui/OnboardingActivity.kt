package com.crudax.launcher.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.R
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private var step = 0
    private lateinit var title: TextView
    private lateinit var body: TextView
    private lateinit var next: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(64, 128, 64, 64)
        }
        title = TextView(this).apply {
            textSize = 24f
            setPadding(0, 0, 0, 24)
        }
        body = TextView(this).apply {
            textSize = 16f
            setPadding(0, 0, 0, 48)
        }
        next = Button(this).apply { text = "Continuer" }
        layout.addView(title)
        layout.addView(body)
        layout.addView(next)
        setContentView(layout)
        showStep()
        next.setOnClickListener { advance() }
    }

    private fun showStep() {
        when (step) {
            0 -> {
                title.text = getString(R.string.welcome_title)
                body.text = getString(R.string.welcome_subtitle) +
                    "\n\nCRUDAX est une couche minimale au-dessus d'Android.\nAucune permission invasive au démarrage."
            }
            1 -> {
                title.text = "Écran d'accueil"
                body.text = "Vous pouvez définir CRUDAX comme launcher par défaut dans les paramètres Android.\n\nCela reste optionnel."
                next.text = "Ouvrir les réglages Home"
            }
            2 -> {
                title.text = "Termux"
                val installed = CrudaxApp.instance.container.termuxManager.isInstalled()
                body.text = if (installed) "Termux détecté. Intégration disponible."
                else "Termux non détecté. CRUDAX fonctionne sans Termux (niveau 0)."
                next.text = "Continuer"
            }
            3 -> {
                title.text = "Shizuku"
                val s = CrudaxApp.instance.container.shizukuManager.isInstalled()
                body.text = if (s) "Shizuku détecté (optionnel)."
                else "Shizuku absent. Fonctionnalités avancées désactivées."
            }
            4 -> {
                title.text = "Prêt"
                body.text = "Glissez vers le haut pour les applications.\nAppui long pour le mode édition.\nAppuyez sur l'horloge pour les paramètres."
                next.text = "Terminer"
            }
        }
    }

    private fun advance() {
        if (step == 1) {
            try {
                startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
            } catch (_: Exception) { }
        }
        step++
        if (step > 4) {
            lifecycleScope.launch {
                CrudaxApp.instance.container.preferences.setOnboardingDone(true)
            }
            finish()
        } else {
            showStep()
        }
    }
}
