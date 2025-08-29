package ir.amirroid.clipshare.ui_models.clipboard

import androidx.compose.ui.text.AnnotatedString
import ir.amirroid.clipshare.domain.models.ClipboardContentDomain

fun ClipboardContentDomain.toUiModel(): ClipboardContentUiModel = when (this) {
    is ClipboardContentDomain.Text -> ClipboardContentUiModel.Text(value, id)
    is ClipboardContentDomain.Image -> ClipboardContentUiModel.Image(path, id)
    is ClipboardContentDomain.Files -> ClipboardContentUiModel.Files(paths, id)
    is ClipboardContentDomain.RichText -> ClipboardContentUiModel.RichText(
        content = AnnotatedString(content), id
    )
}