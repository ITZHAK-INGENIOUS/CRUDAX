package com.crudax.launcher.files

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import com.crudax.launcher.cruda.ActionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultFileManager(private val context: Context) : FileManager {

    override fun openPreferredFileManager(path: String?) {
        // Prefer system document UI or any registered file manager
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = DocumentsContract.Document.MIME_TYPE_DIR
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Ouvrir avec…"))
        } catch (_: Exception) {
            // Fallback: open DocumentsUI if present
            try {
                val docs = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                docs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(docs)
            } catch (_: Exception) { /* ignore */ }
        }
    }

    override fun createDocumentIntent(fileName: String, mimeType: String): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
    }

    override fun openDocumentTreeIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    }

    override suspend fun createFileViaSaf(uri: Uri, content: String): ActionResult =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(content.toByteArray(Charsets.UTF_8))
                }
                ActionResult(true, "Fichier créé")
            } catch (e: Exception) {
                ActionResult(false, e.message ?: "Erreur écriture")
            }
        }

    override fun guessMimeType(fileName: String): String {
        val ext = fileName.substringAfterLast('.', "")
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
            ?: when (ext.lowercase()) {
                "sh", "bash" -> "application/x-sh"
                "c", "h" -> "text/x-c"
                "md" -> "text/markdown"
                "json" -> "application/json"
                "py" -> "text/x-python"
                else -> "text/plain"
            }
    }
}
