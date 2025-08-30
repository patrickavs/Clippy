package ir.amirroid.clipshare.clipboard.models

enum class ClipboardContentType {
    TEXT, FILES, IMAGE, HTML, RTF
}

data class ClipboardContentRequest(
    val type: ClipboardContentType,
    val data: String
)
