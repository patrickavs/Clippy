package ir.amirroid.clipshare.domain.models

sealed interface ClipboardContentDomain {
    data class Text(val value: String, override val id: Long) : ClipboardContentDomain
    data class Image(val path: String, override val id: Long) : ClipboardContentDomain
    data class Files(val paths: List<String>, override val id: Long) : ClipboardContentDomain
    data class RichText(val content: String, val type: Type, override val id: Long) :
        ClipboardContentDomain {
        enum class Type { RTF, HTML }
    }

    val id: Long
}