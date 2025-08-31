package ir.amirroid.clipshare.history.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import ir.amirroid.clipshare.common.compose.RelativeTimeText
import ir.amirroid.clipshare.design_system.components.AppCard
import ir.amirroid.clipshare.design_system.components.AppIconButton
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.ui_models.clipboard.ClipboardContentUiModel

private fun getContentTypeText(content: ClipboardContentUiModel) = when (content) {
    is ClipboardContentUiModel.Html -> "HTML"
    is ClipboardContentUiModel.RichText -> "RICH TEXT"
    is ClipboardContentUiModel.Files -> "FILES"
    is ClipboardContentUiModel.Image -> "IMAGE"
    is ClipboardContentUiModel.Text -> "TEXT"
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ClipboardContentSection(
    clipboardContentUiModel: ClipboardContentUiModel,
    onCopy: () -> Unit,
    onDelete: (() -> Unit)? = null,
    maxHeight: Dp = 250.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val alpha by animateFloatAsState(if (isHovered) 1f else 0.6f)

    var isExpanded by remember { mutableStateOf(false) }
    var needToExpand by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    var contentHeight by remember { mutableStateOf(maxHeight) }
    val currentHeight by animateDpAsState(
        if (isExpanded) contentHeight else maxHeight
    )

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource),
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                AppText(
                    text = getContentTypeText(clipboardContentUiModel),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clipToBounds()
                        .then(
                            when {
                                !needToExpand -> Modifier.heightIn(max = maxHeight)
                                else -> Modifier.height(currentHeight)
                            }
                        )
                        .then(
                            if (isExpanded && needToExpand) Modifier.padding(bottom = 36.dp)
                            else Modifier
                        ),
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight(unbounded = true, align = Alignment.Top)
                            .fillMaxWidth()
                            .onSizeChanged {
                                needToExpand = it.height > with(density) { maxHeight.toPx() }
                                if (needToExpand && contentHeight != 0.dp) {
                                    contentHeight = with(density) { it.height.toDp() + 32.dp }
                                }
                            }
                    ) {
                        content()
                    }


                    if (needToExpand && !isExpanded) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                                .height(86.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                        ),
                                    )
                                )
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                        .alpha(alpha)
                ) {
                    RelativeTimeText(
                        time = clipboardContentUiModel.createdAt,
                        style = MaterialTheme.typography.labelMedium
                    )
                    onDelete?.let {
                        AppIconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    AppIconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CopyAll,
                            contentDescription = "Copy",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }


            if (needToExpand) {
                ToggleButton(
                    checked = isExpanded,
                    onCheckedChange = { isExpanded = it },
                ) {
                    AppText(if (isExpanded) "Collapse" else "Expand")
                }
            }
        }
    }
}