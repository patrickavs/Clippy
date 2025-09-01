package ir.amirroid.clipshare.data.di

import ir.amirroid.clipshare.data.repository.clipboard.ClipboardRepositoryImpl
import ir.amirroid.clipshare.data.repository.devices.DevicesRepositoryImpl
import ir.amirroid.clipshare.domain.repository.clipboard.ClipboardRepository
import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::ClipboardRepositoryImpl).bind<ClipboardRepository>()
    singleOf(::DevicesRepositoryImpl).bind<DevicesRepository>()
}