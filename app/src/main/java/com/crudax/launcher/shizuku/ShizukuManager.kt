package com.crudax.launcher.shizuku

interface ShizukuManager {
    fun isInstalled(): Boolean
    fun isAvailable(): Boolean
    fun getDiagnostic(): ShizukuDiagnostic
}

data class ShizukuDiagnostic(
    val installed: Boolean,
    val available: Boolean,
    val binderAlive: Boolean = false
)
