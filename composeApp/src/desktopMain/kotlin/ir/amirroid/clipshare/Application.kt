package ir.amirroid.clipshare

import io.github.vinceglb.autolaunch.AutoLaunch
import ir.amirroid.clipshare.di.DependencyInjection
import ir.amirroid.clipshare.process.service.ClipboardProcessorService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Application : KoinComponent {
    private val processor: ClipboardProcessorService by inject()

    suspend fun start() {
        DependencyInjection.configure {
            modules(appModule)
        }
        enableAutoLaunch()
        processor.start()
    }

    fun close() {
        processor.dispose()
    }

    suspend fun enableAutoLaunch() {
        val autoLaunch = AutoLaunch(appPackageName = "ClipShare")
        if (autoLaunch.isEnabled().not()) autoLaunch.enable()
    }
}