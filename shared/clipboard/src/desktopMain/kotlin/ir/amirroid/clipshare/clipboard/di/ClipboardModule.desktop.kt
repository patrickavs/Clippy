package ir.amirroid.clipshare.clipboard.di

import ir.amirroid.clipshare.clipboard.manager.ClipboardManager
import ir.amirroid.clipshare.clipboard.manager.DesktopClipboardManagerImpl
import ir.amirroid.clipshare.clipboard.utils.ClipboardContentRequestConverter
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val clipboardModule = module {
    singleOf(::ClipboardContentRequestConverter)
    singleOf(::DesktopClipboardManagerImpl).bind<ClipboardManager>()
}
