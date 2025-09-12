package ir.amirroid.clipshare.process.handler

import ir.amirroid.clipshare.clipboard.models.ClipboardContent

interface ClipboardHandler {
    fun handle(content: ClipboardContent)
}