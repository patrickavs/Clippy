package ir.amirroid.clipshare

import androidx.compose.ui.window.ComposeUIViewController
import ir.amirroid.clipshare.di.DependencyInjection

fun MainViewController() = ComposeUIViewController(
    configure = {
        DependencyInjection.configure()
    }
) { App() }