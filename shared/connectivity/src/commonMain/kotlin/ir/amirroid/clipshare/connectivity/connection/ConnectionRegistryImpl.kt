package ir.amirroid.clipshare.connectivity.connection

import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ConnectionRegistryImpl(
    dispatcher: CoroutineDispatcher
) : ConnectionRegistry {
    private val connections = mutableMapOf<String, PeerToPeerConnectionService>()
    private val _statusFlow = MutableStateFlow<Map<String, ConnectionStatus>>(emptyMap())
    override val allConnectionStatus: Flow<Map<String, ConnectionStatus>> = _statusFlow

    private val scope = CoroutineScope(dispatcher)

    override fun addConnection(deviceId: String, connection: PeerToPeerConnectionService) {
        scope.launch {
            connections[deviceId] = connection


            connection.connectionStatus.collect { updateStatus(deviceId, it) }
        }
    }

    override fun getConnection(deviceId: String): PeerToPeerConnectionService? =
        connections[deviceId]

    override fun removeConnection(deviceId: String) {
        connections[deviceId]?.close()
        connections.remove(deviceId)
        _statusFlow.value = _statusFlow.value - deviceId
    }

    override fun allConnections(): List<PeerToPeerConnectionService> = connections.values.toList()
    override fun allConnectionDevices(): List<String> = connections.keys.toList()

    private fun updateStatus(deviceId: String, status: ConnectionStatus) {
        _statusFlow.value = _statusFlow.value + (deviceId to status)
    }

    override fun close() {
        connections.values.forEach { it.close() }
        scope.cancel()
    }
}