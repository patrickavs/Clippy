package ir.amirroid.clipshare.clipboard.models

sealed interface ClipboardContent {
    data class Text(val value: String) : ClipboardContent
    data class Image(val bytes: ByteArray) : ClipboardContent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            return bytes.contentEquals((other as Image).bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    data class Files(val paths: List<String>) : ClipboardContent
    data class Rtf(val content: String) : ClipboardContent
    data class Html(val data: HtmlWithPlainText) : ClipboardContent
}