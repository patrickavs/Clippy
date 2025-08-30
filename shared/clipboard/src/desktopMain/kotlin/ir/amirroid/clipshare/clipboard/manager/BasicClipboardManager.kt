package ir.amirroid.clipshare.clipboard.manager

import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.models.ClipboardContentRequest
import ir.amirroid.clipshare.clipboard.utils.ClipboardContentRequestConverter
import ir.amirroid.clipshare.clipboard.utils.TransferableConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.Transferable

abstract class BasicClipboardManager(
    private val contentRequestConverter: ClipboardContentRequestConverter
) : ClipboardManager, ClipboardOwner {
    protected var job: Job? = null
    protected val scope = CoroutineScope(Dispatchers.Default)

    protected val systemClipboard: Clipboard by lazy { Toolkit.getDefaultToolkit().systemClipboard }

    protected fun readClipboard(): ClipboardContent? {
        return runCatching {
            val transferable: Transferable = systemClipboard.getContents(null)
            TransferableConverter.fromTransferable(transferable)
        }.getOrNull()
    }

    override suspend fun setContent(request: ClipboardContentRequest) {
        val transferable = contentRequestConverter.fromRequest(request)
        systemClipboard.setContents(transferable, this)
    }

    override fun lostOwnership(clipboard: Clipboard, contents: Transferable) {
        println("Ownership lost")
    }
}