package ir.amirroid.clipshare.clipboard.manager

import android.content.Context
import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import kotlinx.serialization.json.Json

class AndroidClipboardManagerImpl(
    context: Context,
    json: Json
) : BasicClipboardManager(context, json),
    android.content.ClipboardManager.OnPrimaryClipChangedListener {
    private var listener: ((ClipboardContent) -> Unit)? = null

    override fun addOnChangedListener(action: (ClipboardContent) -> Unit) {
        listener = action
        clipboardManager.addPrimaryClipChangedListener(this)
    }

    override fun dispose() {
        clipboardManager.removePrimaryClipChangedListener(this)
        listener = null
    }

    override fun onPrimaryClipChanged() {
        if (listener == null) return

        val content = getClipboardContent()
        if (content == lastContent) return

        content?.let(listener!!::invoke)
        lastContent = content
    }
}