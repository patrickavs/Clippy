package ir.amirroid.clipshare.clipboard.utils

import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.models.HtmlWithPlainText
import java.awt.Image
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

object TransferableConverter {
    fun fromTransferable(transferable: Transferable): ClipboardContent? {
        return try {
            when {
                transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) -> {
                    val files = transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                    ClipboardContent.Files(files.filterIsInstance<File>().map { it.absolutePath })
                }

                transferable.isDataFlavorSupported(DataFlavor.allHtmlFlavor) -> {
                    val html = transferable.getTransferData(DataFlavor.allHtmlFlavor) as String
                    val text = if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        transferable.getTransferData(DataFlavor.stringFlavor) as String
                    } else ""
                    ClipboardContent.Html(HtmlWithPlainText(text, html))
                }

                transferable.isDataFlavorSupported(Flavors.rtfInputStream) -> {
                    val inputStream = transferable.getTransferData(Flavors.rtfInputStream) as InputStream
                    val content = inputStream.bufferedReader().use { it.readText() }
                    ClipboardContent.Rtf(content)
                }

                transferable.isDataFlavorSupported(DataFlavor.imageFlavor) -> {
                    val image = transferable.getTransferData(DataFlavor.imageFlavor) as Image
                    val bytes = imageToByteArray(image)
                    if (bytes != null) ClipboardContent.Image(bytes) else null
                }

                transferable.isDataFlavorSupported(DataFlavor.stringFlavor) -> {
                    val text = transferable.getTransferData(DataFlavor.stringFlavor) as String
                    ClipboardContent.Text(text)
                }

                else -> null
            }
        } catch (e: Exception) {
            // Ignore ClassNotFoundExceptions caused by IDEs or unsupported custom clipboards
            if (e !is ClassNotFoundException) {
                e.printStackTrace()
            }
            null
        }
    }

    private fun imageToByteArray(image: Image): ByteArray? {
        val bufferedImage = if (image is BufferedImage) {
            image
        } else {
            val bimg = BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
            )
            val g2d = bimg.createGraphics()
            g2d.drawImage(image, 0, 0, null)
            g2d.dispose()
            bimg
        }
        val baos = ByteArrayOutputStream()
        return try {
            ImageIO.write(bufferedImage, "png", baos)
            baos.toByteArray()
        } catch (e: Exception) {
            null
        }
    }
}
