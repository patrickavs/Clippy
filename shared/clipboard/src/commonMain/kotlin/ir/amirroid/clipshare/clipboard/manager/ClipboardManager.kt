package ir.amirroid.clipshare.clipboard.manager

import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.models.ClipboardContentRequest

interface ClipboardManager {
    fun addOnChangedListener(action: (ClipboardContent) -> Unit)
    suspend fun setContent(request: ClipboardContentRequest)
    fun dispose()
}