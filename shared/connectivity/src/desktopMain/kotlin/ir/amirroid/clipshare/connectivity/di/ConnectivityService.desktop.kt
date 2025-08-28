package ir.amirroid.clipshare.connectivity.di

import ir.amirroid.clipshare.connectivity.broadcast.DesktopDeviceBroadcastServiceImpl
import ir.amirroid.clipshare.connectivity.broadcast.DeviceBroadcastService
import ir.amirroid.clipshare.connectivity.device.DesktopDeviceUidProviderImpl
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.discovery.DesktopDeviceDiscoveryServiceImpl
import ir.amirroid.clipshare.connectivity.discovery.DeviceDiscoveryService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val connectivityModule = module {
    singleOf(::DesktopDeviceUidProviderImpl).bind<DeviceUidProvider>()
    singleOf(::DesktopDeviceDiscoveryServiceImpl).bind<DeviceDiscoveryService>()
    singleOf(::DesktopDeviceBroadcastServiceImpl).bind<DeviceBroadcastService>()
}