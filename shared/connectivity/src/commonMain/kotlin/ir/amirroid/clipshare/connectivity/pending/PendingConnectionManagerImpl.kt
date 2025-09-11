package ir.amirroid.clipshare.connectivity.pending

import ir.amirroid.clipshare.connectivity.models.DeviceInfo
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PendingConnectionManagerImpl : PendingConnectionManager {

    private val _pendingConnections = MutableStateFlow(emptyList<DeviceInfo>())
    override val pendingConnections: StateFlow<List<DeviceInfo>> = _pendingConnections

    private val pendingMessages = mutableMapOf<String, SignalingMessage>()
    private val mutex = Mutex()

    override suspend fun addNewPending(deviceInfo: DeviceInfo, offerMessage: SignalingMessage) {
        _pendingConnections.update { it + deviceInfo }
        mutex.withLock {
            pendingMessages[deviceInfo.id] = offerMessage
        }
    }

    override suspend fun removePending(deviceId: String) {
        _pendingConnections.update { connections ->
            connections.filter { it.id != deviceId }
        }
        mutex.withLock {
            pendingMessages.remove(deviceId)
        }
    }

    override suspend fun getMessage(deviceId: String): SignalingMessage? = mutex.withLock {
        pendingMessages[deviceId]
    }
}