package com.crudax.launcher.termux

import com.crudax.launcher.cruda.ActionResult

interface TermuxManager {
    fun isInstalled(): Boolean
    fun isApiInstalled(): Boolean
    fun isGuiInstalled(): Boolean
    fun isRunCommandAllowed(): Boolean
    fun openTermux()
    fun openNewSession()
    suspend fun runCommand(
        command: String,
        workDir: String? = null,
        timeoutMs: Long = 30_000L
    ): ActionResult
    suspend fun listInstalledPackages(): List<TermuxPackage>
    suspend fun getPackageVersion(pkg: String): String?
    fun getDiagnostic(): TermuxDiagnostic
}

data class TermuxPackage(
    val name: String,
    val version: String?,
    val installed: Boolean
)

data class TermuxDiagnostic(
    val installed: Boolean,
    val version: String?,
    val runCommandAllowed: Boolean,
    val allowExternalApps: Boolean?, // null = unknown without access
    val apiInstalled: Boolean,
    val guiInstalled: Boolean
)
