package ir.amirroid.clipshare.process.di

import ir.amirroid.clipshare.process.storage.DesktopPlatformStorageImpl
import ir.amirroid.clipshare.process.storage.PlatformStorage
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

actual fun Module.configure() {
    singleOf(::DesktopPlatformStorageImpl).bind<PlatformStorage>()
}