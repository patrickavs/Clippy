package ir.amirroid.clipshare.data.repository.devices

import ir.amirroid.clipshare.connectivity.broadcast.DeviceBroadcastService
import ir.amirroid.clipshare.connectivity.discovery.DeviceDiscoveryService
import ir.amirroid.clipshare.data.mapper.toDomain
import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository
import kotlinx.coroutines.flow.map

class DevicesRepositoryImpl(
    private val discoveryService: DeviceDiscoveryService,
    private val broadcastService: DeviceBroadcastService
) : DevicesRepository {
    override val nearbyDevices = discoveryService.incoming.map { devices ->
        devices.map { it.toDomain() }
    }
    override val isBroadcasting = broadcastService.isStarted

    override suspend fun startDiscoveringNearbyDevices() {
        discoveryService.startDiscovery()
    }

    override suspend fun stopDiscoveringNearbyDevices() {
        discoveryService.stopDiscovery()
    }

    override suspend fun startBroadcastingMyDevice() {
        broadcastService.startBroadcasting()
    }

    override suspend fun stopBroadcastingMyDevice() {
        broadcastService.stopBroadcasting()
    }
}