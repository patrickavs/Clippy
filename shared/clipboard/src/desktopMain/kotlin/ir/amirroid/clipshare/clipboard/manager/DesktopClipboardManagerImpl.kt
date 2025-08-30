package ir.amirroid.clipshare.clipboard.manager

import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.utils.ClipboardContentRequestConverter
import java.awt.datatransfer.FlavorListener

class DesktopClipboardManagerImpl(
    contentRequestConverter: ClipboardContentRequestConverter
) : BasicClipboardManager(contentRequestConverter) {
    private var listener: FlavorListener? = null

    override fun addOnChangedListener(action: (ClipboardContent) -> Unit) {
        listener?.let { systemClipboard.removeFlavorListener(it) }

        listener = createListener(action = action)
        systemClipboard.addFlavorListener(listener)
    }

    private fun createListener(action: (ClipboardContent) -> Unit): FlavorListener {
        return FlavorListener {
            if (it == null) return@FlavorListener
            val content = readClipboard()
            if (content != null) {
                action(content)
            }
        }
    }


    override fun dispose() {
        listener?.let { systemClipboard.removeFlavorListener(it) }
        listener = null
    }
}