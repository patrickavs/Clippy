package ir.amirroid.clipshare.storage

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val storageModule = module {
    singleOf(::AndroidPlatformStorageImpl).bind<PlatformStorage>()
}