package ir.amirroid.clipshare.process.di

import ir.amirroid.clipshare.process.service.ClipboardProcessorService
import ir.amirroid.clipshare.process.service.ClipboardProcessorServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val processorModule = module {
    singleOf(::ClipboardProcessorServiceImpl).bind<ClipboardProcessorService>()
}