package com.crudax.launcher.ui

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crudax.launcher.CrudaxApp
import com.crudax.launcher.R
import com.crudax.launcher.apps.AppInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class AppsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private var allApps: List<AppInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.applications)

        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val search = findViewById<SearchView>(R.id.search)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText.orEmpty())
                return true
            }
        })

        loadApps()
    }

    private fun loadApps() {
        allApps = CrudaxApp.instance.container.appManager.getLaunchableApps()
        recycler.adapter = AppAdapter(allApps) { app ->
            CrudaxApp.instance.container.appManager.launchApp(app.packageName)
        }
    }

    private fun filter(q: String) {
        val filtered = if (q.isBlank()) allApps
        else allApps.filter {
            it.label.contains(q, true) || it.packageName.contains(q, true)
        }
        recycler.adapter = AppAdapter(filtered) { app ->
            CrudaxApp.instance.container.appManager.launchApp(app.packageName)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class AppAdapter(
    private val items: List<AppInfo>,
    private val onClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.icon)
        val title: TextView = v.findViewById(R.id.title)
        val subtitle: TextView = v.findViewById(R.id.subtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val app = items[position]
        holder.title.text = app.label
        holder.subtitle.text = app.packageName
        holder.icon.setImageDrawable(app.icon)
        holder.itemView.setOnClickListener { onClick(app) }
    }

    override fun getItemCount() = items.size
}
