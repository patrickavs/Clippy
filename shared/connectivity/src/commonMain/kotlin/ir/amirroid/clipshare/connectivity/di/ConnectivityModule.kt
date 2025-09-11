package ir.amirroid.clipshare.connectivity.di

import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistryImpl
import ir.amirroid.clipshare.connectivity.pending.PendingConnectionManager
import ir.amirroid.clipshare.connectivity.pending.PendingConnectionManagerImpl
import ir.amirroid.clipshare.connectivity.signaling.SignalingService
import ir.amirroid.clipshare.connectivity.signaling.SignalingServiceImpl
import ir.amirroid.clipshare.connectivity.sync.SyncService
import ir.amirroid.clipshare.connectivity.sync.SyncServiceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun Module.configureModule()

val connectivityModule = module {
    configureModule()
    singleOf(::PendingConnectionManagerImpl).bind<PendingConnectionManager>()
    singleOf(::SignalingServiceImpl).bind<SignalingService>()
    singleOf(::ConnectionRegistryImpl).bind<ConnectionRegistry>()
    singleOf(::SyncServiceImpl).bind<SyncService>()
}