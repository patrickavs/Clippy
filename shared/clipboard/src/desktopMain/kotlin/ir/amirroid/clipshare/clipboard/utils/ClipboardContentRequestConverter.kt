package ir.amirroid.clipshare.clipboard.utils

import ir.amirroid.clipshare.clipboard.models.ClipboardContentRequest
import ir.amirroid.clipshare.clipboard.models.ClipboardContentType
import ir.amirroid.clipshare.clipboard.models.HtmlWithPlainText
import ir.amirroid.clipshare.common.app.events.EventBus
import ir.amirroid.clipshare.common.app.models.NotificationRequest
import kotlinx.serialization.json.Json
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

class ClipboardContentRequestConverter(private val json: Json) {
    suspend fun fromRequest(request: ClipboardContentRequest): Transferable? {
        return when (request.type) {
            ClipboardContentType.TEXT -> {
                StringSelection(request.data).also {
                    EventBus.publish(NotificationRequest("Text Copied"))
                }
            }

            ClipboardContentType.IMAGE -> {
                createTransferable(DataFlavor.imageFlavor) {
                    javax.imageio.ImageIO.read(java.io.File(request.data))
                }.also {
                    EventBus.publish(NotificationRequest("Image Copied"))
                }
            }


            ClipboardContentType.FILES -> {
                val files = json.decodeFromString<List<String>>(request.data)
                createTransferable(DataFlavor.javaFileListFlavor) {
                    files.map { java.io.File(it.trim()) }
                }.also {
                    if (files.size == 1) {
                        EventBus.publish(NotificationRequest("File Copied"))
                    } else {
                        EventBus.publish(NotificationRequest("${files.size} Files Copied"))
                    }
                }
            }

            ClipboardContentType.HTML -> {
                val htmlWithPlainText = json.decodeFromString<HtmlWithPlainText>(request.data)
                createTransferable(arrayOf(DataFlavor.allHtmlFlavor, DataFlavor.stringFlavor)) {
                    if (it == DataFlavor.stringFlavor) htmlWithPlainText.text else htmlWithPlainText.html
                }.also {
                    EventBus.publish(NotificationRequest("Rich Text Copied"))
                }
            }

            ClipboardContentType.RTF -> {
                createTransferable(Flavors.rtfInputStream) {
                    java.io.ByteArrayInputStream(request.data.toByteArray())
                }.also {
                    EventBus.publish(NotificationRequest("Rich Text Copied"))
                }
            }
        }
    }

    private fun createTransferable(
        flavors: Array<DataFlavor>,
        createData: (DataFlavor) -> Any?
    ): Transferable {
        return object : Transferable {
            override fun getTransferDataFlavors() = flavors

            override fun isDataFlavorSupported(f: DataFlavor): Boolean {
                return flavors.contains(f)
            }

            override fun getTransferData(f: DataFlavor): Any {
                return createData(f) ?: UnsupportedFlavorException(f)
            }
        }
    }

    private fun createTransferable(
        flavor: DataFlavor,
        createData: (DataFlavor) -> Any?
    ): Transferable {
        return object : Transferable {
            override fun getTransferDataFlavors() = arrayOf(flavor)

            override fun isDataFlavorSupported(f: DataFlavor): Boolean {
                return flavor == f
            }

            override fun getTransferData(f: DataFlavor): Any {
                return createData(f) ?: UnsupportedFlavorException(f)
            }
        }
    }
}