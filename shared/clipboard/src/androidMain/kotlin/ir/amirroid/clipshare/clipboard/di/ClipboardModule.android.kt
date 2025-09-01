package ir.amirroid.clipshare.clipboard.di

import ir.amirroid.clipshare.clipboard.manager.PoolingAndroidClipboardManagerImpl
import ir.amirroid.clipshare.clipboard.manager.ClipboardManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val clipboardModule = module {
    singleOf(::PoolingAndroidClipboardManagerImpl).bind<ClipboardManager>()
}