package ir.amirroid.clipshare.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.amirroid.clipshare.design_system.components.AppButton
import ir.amirroid.clipshare.design_system.components.AppIconButton
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.design_system.components.AppTopAppBar
import ir.amirroid.clipshare.history.components.ClipboardContentView
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun rememberColumnCount(): Int {
    val windowSize = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthDp = with(density) { windowSize.containerSize.width.toDp().value }

    return calculateColumns(widthDp)
}

fun calculateColumns(widthDp: Float): Int = when {
    widthDp >= 1400 -> 4
    widthDp >= 1000 -> 3
    widthDp >= 700 -> 2
    else -> 1
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardHistoryScreen(
    viewModel: ClipboardHistoryViewModel = koinViewModel()
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    val columns = rememberColumnCount()
    val showDeleteDialog = viewModel.showDeleteDialog

    Column {
        AppTopAppBar(
            title = {
                AppText("History")
            },
            actions = {
                AppIconButton(onClick = { viewModel.showDeleteDialog = true }) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null
                    )
                }
            }
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(columns),
            contentPadding = PaddingValues(12.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(history, key = { it.id }) { content ->
                Box(Modifier.animateItem()) {
                    ClipboardContentView(
                        content = content,
                        onCopy = { viewModel.setClipboardPrimaryContent(content.id) },
                        onDelete = { viewModel.deleteContent(content.id) },
                        onCopyFile = viewModel::setFileClipboardPrimaryContent
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteDialog = false },
            title = { AppText("Delete all history?") },
            text = { AppText("Are you sure you want to delete all clipboard history? This action cannot be undone.") },
            confirmButton = {
                AppButton(
                    onClick = {
                        viewModel.clearAll()
                        viewModel.showDeleteDialog = false
                    }
                ) {
                    AppText("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteDialog = false }) {
                    AppText("Cancel")
                }
            }
        )
    }
}