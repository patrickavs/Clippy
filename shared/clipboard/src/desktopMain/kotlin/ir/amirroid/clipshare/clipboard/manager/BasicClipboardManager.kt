package ir.amirroid.clipshare.clipboard.manager

import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.utils.ClipboardContentConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.Transferable

abstract class BasicClipboardManager() : ClipboardManager, ClipboardOwner {
    protected var job: Job? = null
    protected val scope = CoroutineScope(Dispatchers.Default)

    protected val systemClipboard: Clipboard by lazy { Toolkit.getDefaultToolkit().systemClipboard }

    protected fun readClipboard(): ClipboardContent? {
        return runCatching {
            val transferable: Transferable = systemClipboard.getContents(null)
            ClipboardContentConverter.fromTransferable(transferable)
        }.getOrNull()
    }

    override fun lostOwnership(clipboard: Clipboard, contents: Transferable) {
        println("Ownership lost")
    }
}