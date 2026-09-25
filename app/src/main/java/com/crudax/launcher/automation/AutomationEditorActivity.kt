package com.crudax.launcher.automation

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.cruda.CrudaAction
import kotlinx.coroutines.launch

class AutomationEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }
        val name = EditText(this).apply {
            hint = "Nom de l'automatisation"
        }
        val desc = EditText(this).apply {
            hint = "Description"
        }
        val save = Button(this).apply { text = "Enregistrer" }
        root.addView(name)
        root.addView(desc)
        root.addView(save)
        setContentView(root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nouvelle automatisation"

        save.setOnClickListener {
            val n = name.text.toString().ifBlank { "Sans nom" }
            val d = desc.text.toString()
            lifecycleScope.launch {
                CrudaxApp.instance.container.automationManager.save(
                    Automation(
                        name = n,
                        description = d,
                        steps = listOf(CrudaAction.OpenTermux())
                    )
                )
                Toast.makeText(this@AutomationEditorActivity, "Enregistré", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
