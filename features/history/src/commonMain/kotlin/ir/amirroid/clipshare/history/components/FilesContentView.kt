package ir.amirroid.clipshare.history.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import ir.amirroid.clipshare.common.compose.RelativeTimeText
import ir.amirroid.clipshare.design_system.components.AppCard
import ir.amirroid.clipshare.design_system.components.AppIconButton
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.ui_models.clipboard.ClipboardContentUiModel


private fun extractFileName(path: String) = path.split("/").lastOrNull().orEmpty()

@Composable
fun FilesContentView(
    content: ClipboardContentUiModel.Files,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(content.paths.size == 1) }

    Column(modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(isExpanded) {
            FilesList(content)
        }

        if (content.paths.size != 1) {
            FilesHeaderCard(
                content = content,
                isExpanded = isExpanded,
                onToggleExpand = { isExpanded = !isExpanded },
                onCopy = onCopy,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun FilesList(content: ClipboardContentUiModel.Files) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = if (content.paths.size == 1) 0.dp else 12.dp)
    ) {
        content.paths.forEach { path ->
            ClipboardContentSection(content, onCopy = {}) {
                AppText(text = extractFileName(path))
            }
        }
    }
}

@Composable
private fun FilesHeaderCard(
    content: ClipboardContentUiModel.Files,
    isExpanded: Boolean,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onToggleExpand: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val alpha by animateFloatAsState(if (isHovered) 1f else .6f)
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f)
    val containerColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceContainerHighest
    )
    val contentColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
    )
    val cornerRadius by animateDpAsState(if (isExpanded) 50.dp else 12.dp)

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            AppText(
                "${content.paths.size} Files",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )

            RelativeTimeText(
                time = content.createdAt,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(alpha)
            )

            AppIconButton(
                onClick = onCopy,
                modifier = Modifier.size(24.dp).alpha(alpha)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CopyAll,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }


            AppIconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp).alpha(alpha)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            AppIconButton(
                onClick = onToggleExpand,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).rotate(rotation)
                )
            }
        }
    }
}