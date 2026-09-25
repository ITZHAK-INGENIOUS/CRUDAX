package com.crudax.launcher.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.R
import com.crudax.launcher.automation.Automation
import com.crudax.launcher.automation.AutomationEditorActivity
import com.crudax.launcher.cruda.risk
import com.crudax.launcher.cruda.ActionRisk
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class AutomationsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_automations)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.automations)

        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            startActivity(Intent(this, AutomationEditorActivity::class.java))
        }

        load()
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        lifecycleScope.launch {
            val list = CrudaxApp.instance.container.automationManager.getAll()
            recycler.adapter = AutomationAdapter(list,
                onRun = { auto -> confirmAndRun(auto) },
                onClick = { /* detail future */ }
            )
        }
    }

    private fun confirmAndRun(auto: Automation) {
        val hasHigh = auto.steps.any { it.risk() == ActionRisk.HIGH }
        if (hasHigh) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage("Cette automatisation contient des actions à risque élevé.\n\n${auto.steps.joinToString("\n") { "• ${it::class.simpleName}" }}")
                .setPositiveButton(R.string.continue_action) { _, _ -> doRun(auto) }
                .setNegativeButton(R.string.cancel, null)
                .show()
        } else {
            doRun(auto)
        }
    }

    private fun doRun(auto: Automation) {
        lifecycleScope.launch {
            val result = CrudaxApp.instance.container.automationManager.run(auto.id)
            Toast.makeText(
                this@AutomationsActivity,
                if (result.success) "OK: ${auto.name}" else "Échec: ${result.message}",
                Toast.LENGTH_LONG
            ).show()
            load()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class AutomationAdapter(
    private val items: List<Automation>,
    private val onRun: (Automation) -> Unit,
    private val onClick: (Automation) -> Unit
) : RecyclerView.Adapter<AutomationAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val subtitle: TextView = v.findViewById(R.id.subtitle)
        val runBtn: TextView = v.findViewById(R.id.runBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_automation, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = items[position]
        holder.title.text = "${a.icon} ${a.name}"
        holder.subtitle.text = a.description.ifEmpty {
            "${a.steps.size} étapes • ${a.runCount} exécutions"
        }
        holder.runBtn.setOnClickListener { onRun(a) }
        holder.itemView.setOnClickListener { onClick(a) }
    }

    override fun getItemCount() = items.size
}
