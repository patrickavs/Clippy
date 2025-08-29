package ir.amirroid.clipshare.ui_models.clipboard

import androidx.compose.ui.text.AnnotatedString

sealed interface ClipboardContentUiModel {
    data class Text(val value: String, override val id: Long) : ClipboardContentUiModel
    data class Image(val path: String, override val id: Long) : ClipboardContentUiModel
    data class Files(val paths: List<String>, override val id: Long) : ClipboardContentUiModel
    data class RichText(val content: AnnotatedString, override val id: Long) : ClipboardContentUiModel

    val id: Long
}