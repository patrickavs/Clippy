package ir.amirroid.clipshare.clipboard.models

import kotlinx.serialization.Serializable

enum class ClipboardContentType {
    TEXT, FILES, IMAGE, HTML, RTF
}

@Serializable
data class ClipboardContentRequest(
    val type: ClipboardContentType,
    val data: String,
)
