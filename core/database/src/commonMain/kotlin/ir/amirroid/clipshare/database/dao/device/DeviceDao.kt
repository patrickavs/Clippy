package ir.amirroid.clipshare.database.dao.device

import ir.amirroid.clipshare.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

interface DeviceDao {
    fun getAllDiscoveredDevices(): Flow<List<DeviceEntity>>
    suspend fun addNewDevice(device: DeviceEntity)
    suspend fun removeDevice(deviceId: String)
    suspend fun checkExistsDeviceById(deviceId: String): Boolean
}