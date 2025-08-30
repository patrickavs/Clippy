package ir.amirroid.clipshare.domain.models

import kotlinx.datetime.LocalDateTime

sealed interface ClipboardContentDomain {
    data class Text(
        val value: String, override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentDomain

    data class Image(
        val path: String, override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentDomain

    data class Files(
        val paths: List<String>, override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentDomain

    data class RichText(
        val content: String, val type: Type, override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentDomain {
        enum class Type { RTF, HTML }
    }

    val id: Long
    val createdAt: LocalDateTime
}