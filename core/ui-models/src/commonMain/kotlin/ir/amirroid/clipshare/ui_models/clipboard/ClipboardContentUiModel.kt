package ir.amirroid.clipshare.ui_models.clipboard

import androidx.compose.ui.text.AnnotatedString
import kotlinx.datetime.LocalDateTime

sealed interface ClipboardContentUiModel {
    data class Text(
        val value: String, override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentUiModel

    data class Image(
        val path: String, override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentUiModel

    data class Files(
        val paths: List<String>, override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentUiModel

    data class RichText(
        val content: AnnotatedString,
        override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentUiModel

    data class Html(
        val content: String,
        override val id: Long,
        override val createdAt: LocalDateTime
    ) : ClipboardContentUiModel

    val id: Long
    val createdAt: LocalDateTime
}