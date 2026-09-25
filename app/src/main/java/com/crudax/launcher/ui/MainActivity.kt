package com.crudax.launcher.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.R
import com.crudax.launcher.automation.AutomationEditorActivity
import com.crudax.launcher.diagnostics.DiagnosticsActivity
import com.crudax.launcher.settings.SettingsActivity
import com.crudax.launcher.termux.TermuxConnectionActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var clockText: TextView
    private lateinit var dateText: TextView
    private lateinit var fabAdd: FloatingActionButton
    private val handler = Handler(Looper.getMainLooper())
    private var editMode = false
    private var clock24h = true
    private var showSeconds = false
    private var showDate = true

    private val clockRunnable = object : Runnable {
        override fun run() {
            updateClock()
            val delay = if (showSeconds) 1000L else 30_000L
            handler.postDelayed(this, delay)
        }
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* handled when needed */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check onboarding
        lifecycleScope.launch {
            val done = CrudaxApp.instance.container.preferences.onboardingDone.first()
            if (!done) {
                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                // Don't finish; user can come back
            }
        }

        setContentView(R.layout.activity_main)
        clockText = findViewById(R.id.clockText)
        dateText = findViewById(R.id.dateText)
        fabAdd = findViewById(R.id.fabAdd)

        setupLinks()
        setupGestures()
        setupClockPrefs()
        updateClock()
        handler.post(clockRunnable)

        fabAdd.setOnClickListener { showAddMenu() }
    }

    private fun setupClockPrefs() {
        lifecycleScope.launch {
            CrudaxApp.instance.container.preferences.clock24h.collect { clock24h = it }
        }
        lifecycleScope.launch {
            CrudaxApp.instance.container.preferences.showSeconds.collect { showSeconds = it }
        }
        lifecycleScope.launch {
            CrudaxApp.instance.container.preferences.showDate.collect {
                showDate = it
                dateText.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }

    private fun updateClock() {
        val cal = Calendar.getInstance()
        val timePattern = when {
            showSeconds && clock24h -> "HH:mm:ss"
            showSeconds && !clock24h -> "hh:mm:ss a"
            clock24h -> "HH:mm"
            else -> "hh:mm a"
        }
        clockText.text = SimpleDateFormat(timePattern, Locale.getDefault()).format(cal.time)

        val day = SimpleDateFormat("EEEE", Locale.FRENCH).format(cal.time)
            .replaceFirstChar { it.uppercase() }
        val dayNum = cal.get(Calendar.DAY_OF_MONTH)
        val month = SimpleDateFormat("MMMM", Locale.FRENCH).format(cal.time)
        dateText.text = "$day $dayNum\n$month"
    }

    private fun setupLinks() {
        findViewById<TextView>(R.id.linkApps).setOnClickListener {
            startActivity(Intent(this, AppsActivity::class.java))
        }
        findViewById<TextView>(R.id.linkAutomations).setOnClickListener {
            startActivity(Intent(this, AutomationsActivity::class.java))
        }
        findViewById<TextView>(R.id.linkFiles).setOnClickListener {
            CrudaxApp.instance.container.fileManager.openPreferredFileManager()
        }
        findViewById<TextView>(R.id.linkTools).setOnClickListener {
            startActivity(Intent(this, ToolsActivity::class.java))
        }

        // Long press on brand → diagnostics (dev)
        findViewById<TextView>(R.id.brandText).setOnLongClickListener {
            startActivity(Intent(this, DiagnosticsActivity::class.java))
            true
        }

        // Tap clock → settings
        clockText.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupGestures() {
        val detector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val dy = e2.y - e1.y
                val dx = e2.x - e1.x
                if (Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > 120) {
                    if (dy < 0) {
                        // swipe up → apps
                        startActivity(Intent(this@MainActivity, AppsActivity::class.java))
                    }
                    // swipe down → future command center
                    return true
                }
                return false
            }

            override fun onLongPress(e: MotionEvent) {
                toggleEditMode()
            }
        })

        findViewById<View>(R.id.root).setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            false
        }
    }

    private fun toggleEditMode() {
        editMode = !editMode
        fabAdd.visibility = if (editMode) View.VISIBLE else View.GONE
        Toast.makeText(
            this,
            if (editMode) "Mode édition" else "Mode normal",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showAddMenu() {
        val options = arrayOf("Widget", "Application", "Automatisation", "Texte")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Ajouter")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> addWidget()
                    1 -> startActivity(Intent(this, AppsActivity::class.java))
                    2 -> startActivity(Intent(this, AutomationEditorActivity::class.java))
                    3 -> Toast.makeText(this, "Texte (v2)", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun addWidget() {
        val wm = CrudaxApp.instance.container.widgetManager
        val id = wm.allocateId()
        val pick = Intent(AppWidgetManagerCompat.ACTION_APPWIDGET_PICK).apply {
            putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, id)
        }
        try {
            startActivity(pick)
        } catch (e: Exception) {
            // Fallback: open system picker via AppWidgetManager
            Toast.makeText(this, "Sélecteur de widgets non disponible", Toast.LENGTH_SHORT).show()
            wm.deleteId(id)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(clockRunnable)
        super.onDestroy()
    }
}

/** Compatibility helper for widget pick action */
object AppWidgetManagerCompat {
    const val ACTION_APPWIDGET_PICK = "android.appwidget.action.APPWIDGET_PICK"
}
