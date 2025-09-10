package ir.amirroid.clipshare.data.repository.devices

import ir.amirroid.clipshare.connectivity.broadcast.DeviceBroadcastService
import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
import ir.amirroid.clipshare.connectivity.discovery.DeviceDiscoveryService
import ir.amirroid.clipshare.data.mapper.toDomain
import ir.amirroid.clipshare.data.mapper.toEntity
import ir.amirroid.clipshare.database.dao.device.DeviceDao
import ir.amirroid.clipshare.domain.models.device.ConnectedDevice
import ir.amirroid.clipshare.domain.models.device.Device
import ir.amirroid.clipshare.domain.models.utils.ConnectionStatus
import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DevicesRepositoryImpl(
    private val discoveryService: DeviceDiscoveryService,
    private val broadcastService: DeviceBroadcastService,
    private val deviceDao: DeviceDao,
    private val connectionRegistry: ConnectionRegistry
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

    override suspend fun connectToDevice(device: Device) {
        deviceDao.addNewDevice(device.toEntity())
    }

    override suspend fun disconnectDevice(device: Device) {
        deviceDao.removeDevice(device.id)
    }

    override fun getConnectedDevices(): Flow<List<ConnectedDevice>> {
        return combine(
            deviceDao.getAllDiscoveredDevices(),
            connectionRegistry.allConnectionStatus
        ) { devices, statusMap ->
            devices.map { device ->
                val status = statusMap[device.id]?.let {
                    ConnectionStatus.valueOf(it.name)
                } ?: ConnectionStatus.CONNECTING
                ConnectedDevice(
                    device = device.toDomain(),
                    connectionStatus = status
                )
            }
        }
    }
}