package com.crudax.launcher.cruda

/**
 * Abstract CRUDA actions.
 * CREATE / READ / UPDATE / DELETE / AUTOMATE
 */
sealed class CrudaAction {
    data class CreateFile(val path: String, val content: String = "", val mimeType: String? = null) : CrudaAction()
    data class CreateDirectory(val path: String) : CrudaAction()
    data class ReadFile(val path: String) : CrudaAction()
    data class ListDirectory(val path: String) : CrudaAction()
    data class UpdateFile(val path: String, val content: String) : CrudaAction()
    data class MoveFile(val from: String, val to: String) : CrudaAction()
    data class CopyFile(val from: String, val to: String) : CrudaAction()
    data class DeleteFile(val path: String) : CrudaAction()
    data class RunCommand(val command: String, val workDir: String? = null, val timeoutMs: Long = 30_000) : CrudaAction()
    data class RunScript(val scriptPath: String, val args: List<String> = emptyList()) : CrudaAction()
    data class LaunchApp(val packageName: String) : CrudaAction()
    data class OpenUrl(val url: String) : CrudaAction()
    data class OpenTermux(val session: String? = null) : CrudaAction()
    data class OpenFileManager(val path: String? = null) : CrudaAction()
    data class Wait(val millis: Long) : CrudaAction()
    data class Notify(val title: String, val message: String) : CrudaAction()
}

enum class ActionRisk {
    LOW,    // read, list, open app
    MEDIUM, // create, modify, move
    HIGH    // delete, uninstall, system commands, Shizuku
}

fun CrudaAction.risk(): ActionRisk = when (this) {
    is CrudaAction.ReadFile,
    is CrudaAction.ListDirectory,
    is CrudaAction.LaunchApp,
    is CrudaAction.OpenUrl,
    is CrudaAction.OpenTermux,
    is CrudaAction.OpenFileManager,
    is CrudaAction.Wait,
    is CrudaAction.Notify -> ActionRisk.LOW

    is CrudaAction.CreateFile,
    is CrudaAction.CreateDirectory,
    is CrudaAction.UpdateFile,
    is CrudaAction.MoveFile,
    is CrudaAction.CopyFile,
    is CrudaAction.RunCommand,
    is CrudaAction.RunScript -> ActionRisk.MEDIUM

    is CrudaAction.DeleteFile -> ActionRisk.HIGH
}

data class ActionResult(
    val success: Boolean,
    val message: String = "",
    val stdout: String = "",
    val stderr: String = "",
    val exitCode: Int? = null,
    val data: Any? = null
)
