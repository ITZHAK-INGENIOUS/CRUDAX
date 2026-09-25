package com.crudax.launcher.automation

import com.crudax.launcher.cruda.ActionResult
import com.crudax.launcher.cruda.CrudaAction

data class Automation(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val icon: String = "⚡",
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastRunAt: Long? = null,
    val runCount: Int = 0,
    val steps: List<CrudaAction> = emptyList(),
    val enabled: Boolean = true
)

data class AutomationLog(
    val id: Long = 0,
    val automationId: Long,
    val automationName: String,
    val timestamp: Long,
    val success: Boolean,
    val message: String,
    val stdout: String = "",
    val stderr: String = "",
    val exitCode: Int? = null
)

interface AutomationManager {
    suspend fun getAll(): List<Automation>
    suspend fun getById(id: Long): Automation?
    suspend fun save(automation: Automation): Long
    suspend fun delete(id: Long)
    suspend fun run(id: Long): ActionResult
    suspend fun getLogs(limit: Int = 50): List<AutomationLog>
    suspend fun clearLogs()
}
