package turmaA.grupoB.LinkStage.ui.common

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream

const val MAX_UPLOAD_BYTES = 6 * 1024 * 1024

data class SelectedUploadFile(
    val name: String,
    val bytes: ByteArray,
)

fun ContentResolver.readUploadFile(uri: Uri): SelectedUploadFile? {
    val name = query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    } ?: uri.lastPathSegment ?: "file"

    val bytes = openInputStream(uri)?.use { input ->
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var totalBytes = 0

        while (true) {
            val bytesRead = input.read(buffer)
            if (bytesRead < 0) break
            totalBytes += bytesRead
            if (totalBytes > MAX_UPLOAD_BYTES) return null
            output.write(buffer, 0, bytesRead)
        }
        output.toByteArray()
    } ?: return null

    return SelectedUploadFile(name = name, bytes = bytes)
}

fun safeUploadFileName(fileName: String): String = fileName
    .replace(Regex("[^A-Za-z0-9._-]"), "_")
    .ifBlank { "file" }
