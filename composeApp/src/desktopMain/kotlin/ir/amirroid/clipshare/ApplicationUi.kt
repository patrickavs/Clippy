package ir.amirroid.clipshare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

object ApplicationUi {
    fun start() = application {
        startWindow {
            App()
        }
    }

    @Composable
    private fun ApplicationScope.startWindow(content: @Composable () -> Unit) {
        Window(
            onCloseRequest = ::exitApplication,
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
}