package ir.amirroid.clipshare.clipboard.manager

import ir.amirroid.clipshare.clipboard.models.ClipboardContent

interface ClipboardManager {
    fun addOnChangedListener(action: (ClipboardContent) -> Unit)
    fun dispose()
}