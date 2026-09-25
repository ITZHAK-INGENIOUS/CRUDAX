package com.crudax.launcher.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.R
import com.crudax.launcher.diagnostics.DiagnosticsActivity
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(android.R.layout.list_content) // simple host
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val ctx = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(ctx)

        // Theme
        val theme = ListPreference(ctx).apply {
            key = "theme"
            title = getString(R.string.theme)
            entries = arrayOf(
                getString(R.string.theme_light),
                getString(R.string.theme_dark),
                getString(R.string.theme_mono_light),
                getString(R.string.theme_mono_dark),
                "Système"
            )
            entryValues = arrayOf("light", "dark", "mono_light", "mono_dark", "system")
            setDefaultValue("system")
            setOnPreferenceChangeListener { _, newValue ->
                when (newValue) {
                    "light", "mono_light" ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "dark", "mono_dark" ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                lifecycleScope.launch {
                    CrudaxApp.instance.container.preferences.setTheme(newValue as String)
                }
                true
            }
        }
        screen.addPreference(theme)

        // Clock 24h
        val clock24 = SwitchPreferenceCompat(ctx).apply {
            key = "clock_24h"
            title = "Format 24 h"
            setDefaultValue(true)
            setOnPreferenceChangeListener { _, v ->
                lifecycleScope.launch {
                    CrudaxApp.instance.container.preferences.setClock24h(v as Boolean)
                }
                true
            }
        }
        screen.addPreference(clock24)

        // Set as home
        val home = Preference(ctx).apply {
            title = getString(R.string.set_as_home)
            setOnPreferenceClickListener {
                try {
                    startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
                } catch (_: Exception) {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
                true
            }
        }
        screen.addPreference(home)

        // Diagnostics
        val diag = Preference(ctx).apply {
            title = getString(R.string.diagnostics)
            setOnPreferenceClickListener {
                startActivity(Intent(requireContext(), DiagnosticsActivity::class.java))
                true
            }
        }
        screen.addPreference(diag)

        // Reset
        val reset = Preference(ctx).apply {
            title = getString(R.string.reset_crudax)
            setOnPreferenceClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.reset_crudax)
                    .setMessage(R.string.reset_confirm)
                    .setPositiveButton(R.string.continue_action) { _, _ ->
                        Toast.makeText(context, "Réinitialisation (v2)", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
                true
            }
        }
        screen.addPreference(reset)

        preferenceScreen = screen
    }
}
