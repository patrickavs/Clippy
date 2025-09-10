package ir.amirroid.clipshare.database.dao.device

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ir.amirroid.clipshare.database.Discovered_deviceQueries
import ir.amirroid.clipshare.database.entity.DeviceEntity
import ir.amirroid.clipshare.database.mapper.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeviceDaoImpl(
    private val discoveredDeviceQueries: Discovered_deviceQueries,
    private val dispatcher: CoroutineDispatcher
) : DeviceDao {
    override fun getAllDiscoveredDevices(): Flow<List<DeviceEntity>> {
        return discoveredDeviceQueries.getAllDevices().asFlow().mapToList(dispatcher)
            .map { devices ->
                devices.map { it.toEntity() }
            }
    }

    override suspend fun addNewDevice(device: DeviceEntity) {
        discoveredDeviceQueries.insertDevice(
            deviceId = device.id, name = device.name, platform = device.platform.name
        ).await()
    }

    override suspend fun removeDevice(deviceId: String) {
        discoveredDeviceQueries.deleteDeviceById(deviceId = deviceId).await()
    }
}