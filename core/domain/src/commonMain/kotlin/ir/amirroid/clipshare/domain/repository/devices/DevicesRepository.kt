package ir.amirroid.clipshare.domain.repository.devices

import ir.amirroid.clipshare.domain.models.device.ConnectedDevice
import ir.amirroid.clipshare.domain.models.device.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DevicesRepository {
    val nearbyDevices: Flow<List<Device>>
    val isBroadcasting: StateFlow<Boolean>

    suspend fun startDiscoveringNearbyDevices()
    suspend fun stopDiscoveringNearbyDevices()

    suspend fun startBroadcastingMyDevice()
    suspend fun stopBroadcastingMyDevice()

    suspend fun connectToDevice(device: Device)
    suspend fun disconnectDevice(deviceId:String)
    fun getConnectedDevices(): Flow<List<ConnectedDevice>>

    fun getPendingConnectionDevices(): Flow<List<Device>>
    suspend fun acceptPendingDevice(targetDeviceId: String)
    suspend fun rejectPendingDevice(targetDeviceId: String)
}