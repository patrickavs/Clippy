package ir.amirroid.clipshare.connectivity.di

import dev.onvoid.webrtc.PeerConnectionFactory
import ir.amirroid.clipshare.connectivity.broadcast.DesktopDeviceBroadcastServiceImpl
import ir.amirroid.clipshare.connectivity.broadcast.DeviceBroadcastService
import ir.amirroid.clipshare.connectivity.device.DesktopDeviceUidProviderImpl
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.discovery.DesktopDeviceDiscoveryServiceImpl
import ir.amirroid.clipshare.connectivity.discovery.DeviceDiscoveryService
import ir.amirroid.clipshare.connectivity.p2p.DesktopWebRtcPeerToPeerConnectionImpl
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import ir.amirroid.clipshare.connectivity.provider.DesktopDeviceInfoProvider
import ir.amirroid.clipshare.connectivity.provider.DeviceInfoProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

actual fun Module.configureModule() {
    singleOf(::DesktopDeviceInfoProvider).bind<DeviceInfoProvider>()
    singleOf(::DesktopDeviceUidProviderImpl).bind<DeviceUidProvider>()
    singleOf(::DesktopDeviceDiscoveryServiceImpl).bind<DeviceDiscoveryService>()
    singleOf(::DesktopDeviceBroadcastServiceImpl).bind<DeviceBroadcastService>()

    single { PeerConnectionFactory() }
    factoryOf(::DesktopWebRtcPeerToPeerConnectionImpl).bind<PeerToPeerConnectionService>()
}