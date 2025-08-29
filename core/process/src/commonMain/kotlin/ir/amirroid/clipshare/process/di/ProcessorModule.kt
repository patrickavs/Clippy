package ir.amirroid.clipshare.process.di

import ir.amirroid.clipshare.process.service.ClipboardProcessorService
import ir.amirroid.clipshare.process.service.ClipboardProcessorServiceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun Module.configure()

val processorModule = module {
    configure()
    singleOf(::ClipboardProcessorServiceImpl).bind<ClipboardProcessorService>()
}