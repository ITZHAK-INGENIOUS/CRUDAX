package com.crudax.launcher.files

import android.net.Uri
import com.crudax.launcher.cruda.ActionResult

interface FileManager {
    fun openPreferredFileManager(path: String? = null)
    fun createDocumentIntent(fileName: String, mimeType: String): android.content.Intent
    fun openDocumentTreeIntent(): android.content.Intent
    suspend fun createFileViaSaf(uri: Uri, content: String): ActionResult
    fun guessMimeType(fileName: String): String
}
