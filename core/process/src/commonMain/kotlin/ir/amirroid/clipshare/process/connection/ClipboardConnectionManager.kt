package ir.amirroid.clipshare.process.connection

import ir.amirroid.clipshare.process.handler.ClipboardHandler

interface ClipboardConnectionManager : ClipboardHandler {
    fun start()
    fun close()
}