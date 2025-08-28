package ir.amirroid.clipshare

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ir.amirroid.clipshare.di.DependencyInjection

fun main() {
    DependencyInjection.configure()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "ClipShare",
        ) {
            App()
        }
    }
}