package com.crudax.launcher.permissions

import android.content.Context

enum class PermissionStatus {
    GRANTED, DENIED, NOT_NEEDED, UNKNOWN
}

data class PermissionInfo(
    val name: String,
    val rationale: String,
    val status: PermissionStatus,
    val feature: String
)

interface PermissionManager {
    fun getAll(): List<PermissionInfo>
    fun isNotificationGranted(): Boolean
    fun isOverlayGranted(): Boolean
    fun canDrawOverlays(): Boolean
}
