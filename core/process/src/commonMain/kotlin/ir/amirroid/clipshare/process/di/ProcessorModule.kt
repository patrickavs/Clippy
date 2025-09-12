package ir.amirroid.clipshare.process.di

import ir.amirroid.clipshare.process.connection.ClipboardConnectionManager
import ir.amirroid.clipshare.process.connection.ClipboardConnectionManagerImpl
import ir.amirroid.clipshare.process.service.ClipboardProcessorService
import ir.amirroid.clipshare.process.service.ClipboardProcessorServiceImpl
import ir.amirroid.clipshare.process.tracker.ClipboardSyncTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val processorModule = module {
    singleOf(::ClipboardSyncTracker)
    singleOf(::ClipboardConnectionManagerImpl).bind<ClipboardConnectionManager>()
    singleOf(::ClipboardProcessorServiceImpl).bind<ClipboardProcessorService>()
}