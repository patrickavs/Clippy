package ir.amirroid.clipshare.ui_models.clipboard

import androidx.compose.ui.text.AnnotatedString
import ir.amirroid.clipshare.domain.models.clipboard.ClipboardContentDomain

fun ClipboardContentDomain.toUiModel(): ClipboardContentUiModel = when (this) {
    is ClipboardContentDomain.Text -> ClipboardContentUiModel.Text(value, id, createdAt)
    is ClipboardContentDomain.Image -> ClipboardContentUiModel.Image(path, id, createdAt)
    is ClipboardContentDomain.Files -> ClipboardContentUiModel.Files(paths, id, createdAt)
    is ClipboardContentDomain.RichText -> {
        if (type == ClipboardContentDomain.RichText.Type.RTF) {
            ClipboardContentUiModel.RichText(
                content = AnnotatedString(content), id, createdAt
            )
        } else {
            ClipboardContentUiModel.Html(
                content = content, id, createdAt
            )
        }
    }
}