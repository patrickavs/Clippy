package ir.amirroid.clipshare

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

object ApplicationUi {
    fun start() = application {
        var isWindowOpened by remember { mutableStateOf(true) }

        SystemTray(
            onShowWindow = { isWindowOpened = true }
        )
        if (isWindowOpened) {
            startWindow(
                onDismissRequest = { isWindowOpened = false }
            ) {
                App()
            }
        }
    }

    @Composable
    private fun startWindow(
        onDismissRequest: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Window(
            onCloseRequest = onDismissRequest,
            title = "ClipShare",
            state = rememberWindowState(
                size = DpSize(600.dp, 500.dp),
                position = WindowPosition.Aligned(Alignment.Center)
            )
        ) {
            SideEffect {
                val currentSize = window.size
                window.minimumSize = Dimension(400, 300)
                window.size = currentSize
            }
            content.invoke()
        }
    }

    @Composable
    fun ApplicationScope.SystemTray(onShowWindow: () -> Unit) {
        Tray(
            icon = rememberVectorPainter(Icons.Rounded.CopyAll),
            state = rememberTrayState()
        ) {
            Item(text = "Show Window", onClick = onShowWindow)
            Item(text = "Exit", onClick = {
                Application.close()
                exitApplication()
            })
        }
    }
}