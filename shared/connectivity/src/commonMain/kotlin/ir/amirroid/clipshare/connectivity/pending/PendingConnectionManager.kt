package ir.amirroid.clipshare.connectivity.pending

import ir.amirroid.clipshare.connectivity.models.DeviceInfo
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import kotlinx.coroutines.flow.StateFlow

interface PendingConnectionManager {
    val pendingConnections: StateFlow<List<DeviceInfo>>

    suspend fun addNewPending(deviceInfo: DeviceInfo, offerMessage: SignalingMessage)
    suspend fun removePending(deviceId: String)
    suspend fun getMessage(deviceId: String): SignalingMessage?
}