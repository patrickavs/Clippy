package ir.amirroid.clipshare.clipboard.manager

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.models.ClipboardContentRequest
import ir.amirroid.clipshare.clipboard.models.ClipboardContentType
import ir.amirroid.clipshare.clipboard.models.HtmlWithPlainText
import ir.amirroid.clipshare.common.app.events.EventBus
import ir.amirroid.clipshare.common.app.models.NotificationRequest
import kotlinx.serialization.json.Json
import java.io.File

abstract class BasicClipboardManager(private val context: Context, private val json: Json) :
    ClipboardManager {
    protected var lastContent: ClipboardContent? = null
    protected val clipboardManager: android.content.ClipboardManager by lazy {
        context.getSystemService(android.content.ClipboardManager::class.java)
    }

    override suspend fun setContent(
        request: ClipboardContentRequest,
        withMessage: Boolean,
        withSaveLastItem: Boolean
    ) {
        when (request.type) {
            ClipboardContentType.TEXT -> {
                val text = request.data
                val clip = ClipData.newPlainText("text", text)
                clipboardManager.setPrimaryClip(clip)
                if (withMessage) EventBus.publish(NotificationRequest("Text Copied"))
            }

            ClipboardContentType.HTML -> {
                val htmlWithPlainText = json.decodeFromString<HtmlWithPlainText>(request.data)
                val item = ClipData.Item(htmlWithPlainText.text, htmlWithPlainText.html)
                val clip = ClipData(
                    "html",
                    arrayOf(ClipDescription.MIMETYPE_TEXT_HTML),
                    item
                )
                clipboardManager.setPrimaryClip(clip)
                if (withMessage) EventBus.publish(NotificationRequest("Rich Text Copied"))
            }

            ClipboardContentType.RTF -> {
                val item = ClipData.Item(request.data)
                val clip = ClipData("rtf", arrayOf("text/rtf"), item)
                clipboardManager.setPrimaryClip(clip)
                if (withMessage) EventBus.publish(NotificationRequest("Rich Text Copied"))
            }

            ClipboardContentType.FILES -> {
                val uris = json.decodeFromString<List<String>>(request.data)
                copyFiles(uris)

                if (withMessage) {
                    if (uris.size == 1) {
                        EventBus.publish(NotificationRequest("File Copied"))
                    } else {
                        EventBus.publish(NotificationRequest("${uris.size} Files Copied"))
                    }
                }
            }

            ClipboardContentType.IMAGE -> {
                copyFiles(listOf(request.data))
                if (withMessage) EventBus.publish(NotificationRequest("Image Copied"))
            }
        }
        if (withSaveLastItem) lastContent = getClipboardContent()
    }

    private fun copyFiles(paths: List<String>) {
        if (paths.isEmpty()) return
        val uris = paths.map {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                File(it)
            )
        }

        val firstUri = uris[0]
        val firstMime =
            context.contentResolver.getType(firstUri) ?: ClipDescription.MIMETYPE_TEXT_PLAIN
        val clip = ClipData(
            "files",
            arrayOf(firstMime),
            ClipData.Item(firstUri)
        )

        for (uri in uris) {
            clip.addItem(ClipData.Item(uri))
        }

        clipboardManager.setPrimaryClip(clip)
    }

    protected fun getClipboardContent(): ClipboardContent? {
        val primaryClip = clipboardManager.primaryClip ?: return null
        if (primaryClip.itemCount == 0) return null

        val item = primaryClip.getItemAt(0)
        val description = primaryClip.description

        return when {
            description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) -> {
                val html = item.htmlText ?: return null
                val plain = item.text?.toString().orEmpty()
                ClipboardContent.Html(HtmlWithPlainText(html = html, text = plain))
            }

            description.hasMimeType("text/rtf") -> {
                val rtf = item.text?.toString() ?: return null
                ClipboardContent.Rtf(rtf)
            }

            description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) -> {
                ClipboardContent.Text(item.text?.toString() ?: return null)
            }

            item.uri != null -> {
                val uri = item.uri!!
                context.contentResolver.openInputStream(uri)?.use { ins ->
                    val filename = uri.lastPathSegment ?: System.currentTimeMillis().toString()
                    val outFile = File(context.cacheDir, filename)
                    ins.copyTo(outFile.outputStream())

                    ClipboardContent.Files(listOf(outFile.path))
                }
            }

            else -> null
        }
    }
}