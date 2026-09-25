package com.crudax.launcher.automation

import android.content.Context
import android.util.Log
import com.crudax.launcher.apps.AppManager
import com.crudax.launcher.cruda.ActionResult
import com.crudax.launcher.cruda.ActionRisk
import com.crudax.launcher.cruda.CrudaAction
import com.crudax.launcher.cruda.risk
import com.crudax.launcher.files.FileManager
import com.crudax.launcher.storage.CrudaxDatabase
import com.crudax.launcher.termux.TermuxManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class DefaultAutomationManager(
    private val context: Context,
    private val database: CrudaxDatabase,
    private val termuxManager: TermuxManager,
    private val fileManager: FileManager,
    private val appManager: AppManager
) : AutomationManager {

    // In-memory store for MVP (Room entities can replace later)
    private val automations = mutableListOf<Automation>()
    private val logs = mutableListOf<AutomationLog>()
    private var nextId = 1L

    init {
        // Seed a couple of example automations
        automations.add(
            Automation(
                id = nextId++,
                name = "Environnement XA",
                description = "Prépare un environnement de travail minimal",
                tags = listOf("XA", "setup"),
                steps = listOf(
                    CrudaAction.OpenTermux(),
                    CrudaAction.Notify("XA", "Environnement prêt")
                )
            )
        )
        automations.add(
            Automation(
                id = nextId++,
                name = "Ouvrir Termux",
                description = "Lance Termux",
                tags = listOf("Termux"),
                steps = listOf(CrudaAction.OpenTermux())
            )
        )
    }

    override suspend fun getAll(): List<Automation> = withContext(Dispatchers.IO) {
        automations.toList()
    }

    override suspend fun getById(id: Long): Automation? = withContext(Dispatchers.IO) {
        automations.find { it.id == id }
    }

    override suspend fun save(automation: Automation): Long = withContext(Dispatchers.IO) {
        if (automation.id == 0L) {
            val new = automation.copy(id = nextId++)
            automations.add(new)
            new.id
        } else {
            val idx = automations.indexOfFirst { it.id == automation.id }
            if (idx >= 0) automations[idx] = automation
            else automations.add(automation)
            automation.id
        }
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        automations.removeAll { it.id == id }
    }

    override suspend fun run(id: Long): ActionResult = withContext(Dispatchers.IO) {
        val auto = automations.find { it.id == id }
            ?: return@withContext ActionResult(false, "Automatisation introuvable")

        val results = mutableListOf<String>()
        var overallSuccess = true
        var lastStdout = ""
        var lastStderr = ""
        var lastExit: Int? = null

        for (step in auto.steps) {
            // HIGH risk steps should have been confirmed by UI before calling run()
            val r = executeStep(step)
            results.add("${step::class.simpleName}: ${r.message}")
            if (!r.success) {
                overallSuccess = false
                lastStderr = r.stderr.ifEmpty { r.message }
                break
            }
            lastStdout = r.stdout
            lastExit = r.exitCode
        }

        val msg = results.joinToString("\n")
        val log = AutomationLog(
            id = System.currentTimeMillis(),
            automationId = auto.id,
            automationName = auto.name,
            timestamp = System.currentTimeMillis(),
            success = overallSuccess,
            message = msg,
            stdout = lastStdout,
            stderr = lastStderr,
            exitCode = lastExit
        )
        logs.add(0, log)
        if (logs.size > 200) logs.removeAt(logs.lastIndex)

        // update run stats
        val idx = automations.indexOfFirst { it.id == id }
        if (idx >= 0) {
            automations[idx] = auto.copy(
                lastRunAt = System.currentTimeMillis(),
                runCount = auto.runCount + 1
            )
        }

        ActionResult(overallSuccess, msg, lastStdout, lastStderr, lastExit)
    }

    private suspend fun executeStep(action: CrudaAction): ActionResult {
        return when (action) {
            is CrudaAction.OpenTermux -> {
                termuxManager.openTermux()
                ActionResult(true, "Termux ouvert")
            }
            is CrudaAction.LaunchApp -> {
                val ok = appManager.launchApp(action.packageName)
                ActionResult(ok, if (ok) "App lancée" else "App introuvable")
            }
            is CrudaAction.RunCommand -> {
                termuxManager.runCommand(action.command, action.workDir, action.timeoutMs)
            }
            is CrudaAction.Wait -> {
                delay(action.millis)
                ActionResult(true, "Attente ${action.millis}ms")
            }
            is CrudaAction.Notify -> {
                // Notification will be posted by Notification helper if permission granted
                ActionResult(true, "Notification: ${action.title}")
            }
            is CrudaAction.OpenFileManager -> {
                fileManager.openPreferredFileManager(action.path)
                ActionResult(true, "Gestionnaire de fichiers")
            }
            is CrudaAction.OpenUrl -> {
                try {
                    val i = android.content.Intent(android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(action.url))
                    i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(i)
                    ActionResult(true, "URL ouverte")
                } catch (e: Exception) {
                    ActionResult(false, e.message ?: "Erreur URL")
                }
            }
            else -> ActionResult(true, "Étape ${action::class.simpleName} (MVP stub)")
        }
    }

    override suspend fun getLogs(limit: Int): List<AutomationLog> = withContext(Dispatchers.IO) {
        logs.take(limit)
    }

    override suspend fun clearLogs() = withContext(Dispatchers.IO) {
        logs.clear()
    }
}
