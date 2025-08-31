package ir.amirroid.clipshare.history.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.ui_models.clipboard.ClipboardContentUiModel

@Composable
internal fun ClipboardContentView(
    content: ClipboardContentUiModel,
    onCopy: () -> Unit,
    onCopyFile: (String) -> Unit,
    onDelete: () -> Unit
) {
    when (content) {
        is ClipboardContentUiModel.Text -> {
            ClipboardContentSection(
                clipboardContentUiModel = content,
                onCopy = onCopy,
                onDelete = onDelete
            ) {
                AppText(content.value)
            }
        }

        is ClipboardContentUiModel.Image -> {
            ClipboardContentSection(
                clipboardContentUiModel = content,
                onCopy = onCopy,
                onDelete = onDelete
            ) {
                AsyncImage(
                    model = content.path,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.FillWidth
                )
            }
        }

        is ClipboardContentUiModel.Files -> {
            FilesContentView(content, onCopy, onCopyFile, onDelete)
        }

        is ClipboardContentUiModel.RichText -> {
            ClipboardContentSection(
                clipboardContentUiModel = content,
                onCopy = onCopy,
                onDelete = onDelete
            ) {
                AppText(content.content)
            }
        }

        is ClipboardContentUiModel.Html -> {
            ClipboardContentSection(
                clipboardContentUiModel = content,
                onCopy = onCopy,
                onDelete = onDelete
            ) {
                HtmlRichText(html = content.content)
            }
        }
    }
}

@Composable
fun HtmlRichText(html: String) {
    val state = rememberRichTextState()

    LaunchedEffect(Unit) {
        if (state.annotatedString.text.isEmpty()) {
            state.setHtml(html)
        }
    }

    RichText(
        state = state
    )
}