package ir.amirroid.clipshare

import ir.amirroid.clipshare.di.DependencyInjection
import ir.amirroid.clipshare.process.service.ClipboardProcessorService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Application : KoinComponent {
    private val processor: ClipboardProcessorService by inject()

    fun start() {
        DependencyInjection.configure {
            modules(appModule)
        }

        processor.start()
    }

    fun close() {
        processor.dispose()
    }
}