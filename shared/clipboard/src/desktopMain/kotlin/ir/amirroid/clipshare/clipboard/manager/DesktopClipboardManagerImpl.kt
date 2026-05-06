package ir.amirroid.clipshare.clipboard.manager

import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.utils.ClipboardContentRequestConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DesktopClipboardManagerImpl(
    contentRequestConverter: ClipboardContentRequestConverter
) : BasicClipboardManager(contentRequestConverter) {

    override fun addOnChangedListener(action: (ClipboardContent) -> Unit) {
        job?.cancel()
        job = scope.launch {
            while (isActive) {
                val content = readClipboard()

                if (content != null && content != lastContent) {
                    lastContent = content
                    action(content)
                }

                delay(1000)
            }
        }
    }

    override fun dispose() {
        job?.cancel()
        job = null
    }
}
