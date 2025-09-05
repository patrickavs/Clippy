package ir.amirroid.clipshare.connectivity.di

import ir.amirroid.clipshare.connectivity.broadcast.AndroidDeviceBroadcastServiceImpl
import ir.amirroid.clipshare.connectivity.broadcast.DeviceBroadcastService
import ir.amirroid.clipshare.connectivity.device.AndroidDeviceUidProviderImpl
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.discovery.AndroidDeviceDiscoveryServiceImpl
import ir.amirroid.clipshare.connectivity.discovery.DeviceDiscoveryService
import ir.amirroid.clipshare.connectivity.p2p.AndroidWebRtcPeerToPeerConnectionImpl
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.webrtc.PeerConnectionFactory

actual fun Module.configureModule() {
    singleOf(::AndroidDeviceUidProviderImpl).bind<DeviceUidProvider>()
    singleOf(::AndroidDeviceDiscoveryServiceImpl).bind<DeviceDiscoveryService>()
    singleOf(::AndroidDeviceBroadcastServiceImpl).bind<DeviceBroadcastService>()
    single<PeerConnectionFactory> { PeerConnectionFactory.builder().createPeerConnectionFactory() }
    singleOf(::AndroidWebRtcPeerToPeerConnectionImpl).bind<PeerToPeerConnectionService>()
}