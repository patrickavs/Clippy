package ir.amirroid.clipshare.domain.repository.devices

import ir.amirroid.clipshare.domain.models.DiscoveredDeviceDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DevicesRepository {
    val nearbyDevices: Flow<List<DiscoveredDeviceDomain>>
    val isBroadcasting: StateFlow<Boolean>

    suspend fun startDiscoveringNearbyDevices()
    suspend fun stopDiscoveringNearbyDevices()

    suspend fun startBroadcastingMyDevice()
    suspend fun stopBroadcastingMyDevice()
}