package ir.amirroid.clipshare.connectivity.di

import android.content.Context
import ir.amirroid.clipshare.connectivity.broadcast.AndroidDeviceBroadcastServiceImpl
import ir.amirroid.clipshare.connectivity.broadcast.DeviceBroadcastService
import ir.amirroid.clipshare.connectivity.device.AndroidDeviceUidProviderImpl
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.discovery.AndroidDeviceDiscoveryServiceImpl
import ir.amirroid.clipshare.connectivity.discovery.DeviceDiscoveryService
import ir.amirroid.clipshare.connectivity.p2p.AndroidWebRtcPeerToPeerConnectionImpl
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import ir.amirroid.clipshare.connectivity.provider.AndroidDeviceInfoProvider
import ir.amirroid.clipshare.connectivity.provider.DeviceInfoProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.webrtc.PeerConnectionFactory
import org.webrtc.PeerConnectionFactory.InitializationOptions

actual fun Module.configureModule() {
    singleOf(::AndroidDeviceInfoProvider).bind<DeviceInfoProvider>()
    singleOf(::AndroidDeviceUidProviderImpl).bind<DeviceUidProvider>()
    singleOf(::AndroidDeviceDiscoveryServiceImpl).bind<DeviceDiscoveryService>()
    singleOf(::AndroidDeviceBroadcastServiceImpl).bind<DeviceBroadcastService>()
    single { createPeerConnectionFactory(androidContext()) }
    factoryOf(::AndroidWebRtcPeerToPeerConnectionImpl).bind<PeerToPeerConnectionService>()
}

private fun createPeerConnectionFactory(context: Context): PeerConnectionFactory {
    val initializationOptions =
        InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
    PeerConnectionFactory.initialize(initializationOptions)


    val options = PeerConnectionFactory.Options()

    return PeerConnectionFactory.builder()
        .setOptions(options)
        .createPeerConnectionFactory()
}