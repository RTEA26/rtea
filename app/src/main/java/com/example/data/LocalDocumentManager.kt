package com.example.data

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID

data class ImportedLocalFile(
    val title: String,
    val localPath: String,
    val sizeLabel: String,
    val fileType: String, // PDF, PNG, MP3, DOC
    val mimeType: String,
    val byteSize: Long
)

object LocalDocumentManager {
    private const val ROOT_FOLDER = "rtea_uploads"
    private const val MAX_FILE_BYTES = 25L * 1024L * 1024L

    suspend fun importFromUri(
        context: Context,
        uri: Uri,
        subfolder: String,
        fallbackPrefix: String = "Archivo",
        fallbackExt: String = "pdf"
    ): ImportedLocalFile = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        var displayName = ""
        var reportedSize = -1L

        runCatching {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIdx >= 0) {
                            displayName = cursor.getString(nameIdx).orEmpty()
                        }
                        val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) {
                            reportedSize = cursor.getLong(sizeIdx)
                        }
                    }
                }
        }

        if (reportedSize > MAX_FILE_BYTES) {
            throw IllegalArgumentException("El archivo supera el límite máximo de 25 MB.")
        }

        val resolvedMime = resolver.getType(uri).orEmpty().ifBlank {
            val ext = displayName.substringAfterLast('.', fallbackExt).lowercase(Locale.ROOT)
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "application/octet-stream"
        }

        val cleanName = displayName.ifBlank {
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(resolvedMime) ?: fallbackExt
            "${fallbackPrefix}_${System.currentTimeMillis() % 10000}.$ext"
        }

        val safeDiskName = cleanName.replace(Regex("[^a-zA-Z0-9._\\-]"), "_").take(80)
        val safeSubfolder = subfolder.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").ifBlank { "general" }
        val targetDir = File(File(context.filesDir, ROOT_FOLDER), safeSubfolder).apply { mkdirs() }
        val targetFile = File(targetDir, "${UUID.randomUUID().toString().take(8)}_$safeDiskName")

        var totalBytes = 0L
        resolver.openInputStream(uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                val buffer = ByteArray(16 * 1024)
                while (true) {
                    val read = input.read(buffer)
                    if (read <= 0) break
                    totalBytes += read
                    if (totalBytes > MAX_FILE_BYTES) {
                        targetFile.delete()
                        throw IllegalArgumentException("El archivo supera el límite máximo de 25 MB.")
                    }
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        } ?: throw IllegalStateException("No se pudo abrir el archivo seleccionado desde tu dispositivo.")

        val actualSize = if (totalBytes > 0L) totalBytes else targetFile.length()
        val fileType = classifyFileType(cleanName, resolvedMime)

        ImportedLocalFile(
            title = cleanName.take(140),
            localPath = targetFile.absolutePath,
            sizeLabel = formatSize(actualSize),
            fileType = fileType,
            mimeType = resolvedMime,
            byteSize = actualSize
        )
    }

    fun classifyFileType(fileName: String, mimeType: String): String {
        val lowerName = fileName.lowercase(Locale.ROOT)
        val lowerMime = mimeType.lowercase(Locale.ROOT)
        return when {
            lowerMime == "application/pdf" || lowerName.endsWith(".pdf") -> "PDF"
            lowerMime.startsWith("image/") ||
                lowerName.endsWith(".png") ||
                lowerName.endsWith(".jpg") ||
                lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".webp") ||
                lowerName.endsWith(".gif") ||
                lowerName.endsWith(".bmp") -> "PNG"
            lowerMime.startsWith("audio/") ||
                lowerName.endsWith(".mp3") ||
                lowerName.endsWith(".wav") ||
                lowerName.endsWith(".m4a") ||
                lowerName.endsWith(".ogg") ||
                lowerName.endsWith(".aac") -> "MP3"
            else -> "DOC"
        }
    }

    fun formatSize(bytes: Long): String {
        if (bytes <= 0L) return "1 KB"
        val kb = bytes / 1024.0
        if (kb < 1024.0) return "${kb.toInt().coerceAtLeast(1)} KB"
        val mb = kb / 1024.0
        return String.format(Locale.US, "%.1f MB", mb)
    }

    fun fileExists(localPath: String): Boolean {
        if (localPath.isBlank()) return false
        val f = File(localPath)
        return f.exists() && f.isFile && f.length() > 0L
    }

    suspend fun readFileBytes(localPath: String, maxBytes: Int = 15 * 1024 * 1024): ByteArray? =
        withContext(Dispatchers.IO) {
            if (localPath.isBlank()) return@withContext null
            val file = File(localPath)
            if (!file.exists() || !file.isFile || file.length() <= 0L || file.length() > maxBytes) {
                return@withContext null
            }
            runCatching { file.readBytes() }.getOrNull()
        }

    fun deleteLocalFile(context: Context, localPath: String) {
        if (localPath.isBlank()) return
        runCatching {
            val rootDir = File(context.filesDir, ROOT_FOLDER).canonicalFile
            val target = File(localPath).canonicalFile
            if (target.path.startsWith(rootDir.path) && target.exists()) {
                target.delete()
            }
        }
    }

    suspend fun renderPdfPreviewPages(localPath: String, maxPages: Int = 3): List<Bitmap> =
        withContext(Dispatchers.IO) {
            if (localPath.isBlank()) return@withContext emptyList()
            val file = File(localPath)
            if (!file.exists() || !file.isFile) return@withContext emptyList()

            val bitmaps = mutableListOf<Bitmap>()
            var pfd: ParcelFileDescriptor? = null
            var renderer: PdfRenderer? = null
            try {
                pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                renderer = PdfRenderer(pfd)
                val count = minOf(renderer.pageCount, maxPages)
                for (i in 0 until count) {
                    renderer.openPage(i).use { page ->
                        val targetWidth = 900
                        val aspect = if (page.width > 0) page.height.toFloat() / page.width.toFloat() else 1.41f
                        val targetHeight = (targetWidth * aspect).toInt().coerceIn(300, 1600)
                        val bmp = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bmp)
                        canvas.drawColor(Color.WHITE)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmaps.add(bmp)
                    }
                }
            } catch (_: Exception) {
                // Corrupted or non-PDF file: return empty preview list gracefully
            } finally {
                runCatching { renderer?.close() }
                runCatching { pfd?.close() }
            }
            bitmaps
        }

    fun openFileExternally(context: Context, localPath: String, mimeType: String): Boolean {
        val file = File(localPath)
        if (!file.exists() || !file.isFile) return false
        return runCatching {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val resolvedMime = mimeType.ifBlank { "*/*" }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, resolvedMime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Abrir archivo"))
            true
        }.getOrDefault(false)
    }

    fun shareFileExternally(
        context: Context,
        localPath: String,
        mimeType: String,
        title: String,
        fallbackText: String
    ) {
        val file = if (localPath.isNotBlank()) File(localPath) else null
        if (file != null && file.exists() && file.isFile) {
            val shared = runCatching {
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val resolvedMime = mimeType.ifBlank { "*/*" }
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = resolvedMime
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    clipData = ClipData.newRawUri(title, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Compartir o imprimir"))
                true
            }.getOrDefault(false)
            if (shared) return
        }

        val textIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, fallbackText)
        }
        context.startActivity(Intent.createChooser(textIntent, "Compartir o imprimir"))
    }
}
