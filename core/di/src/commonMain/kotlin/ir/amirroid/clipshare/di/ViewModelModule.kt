package ir.amirroid.clipshare.di

import ir.amirroid.clipshare.devices.DevicesViewModel
import ir.amirroid.clipshare.history.ClipboardHistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::ClipboardHistoryViewModel)
    viewModelOf(::DevicesViewModel)
}