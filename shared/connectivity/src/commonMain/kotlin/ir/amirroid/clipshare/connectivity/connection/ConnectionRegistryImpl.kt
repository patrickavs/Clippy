package ir.amirroid.clipshare.connectivity.connection

import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ConnectionRegistryImpl(
    dispatcher: CoroutineDispatcher
) : ConnectionRegistry {
    private val connections = mutableMapOf<String, PeerToPeerConnectionService>()
    private val connectionTimes = mutableMapOf<String, Long>()
    private val _statusFlow = MutableStateFlow<Map<String, ConnectionStatus>>(emptyMap())
    override val allConnectionStatus: Flow<Map<String, ConnectionStatus>> = _statusFlow

    private val scope = CoroutineScope(dispatcher)

    @OptIn(ExperimentalTime::class)
    override fun addConnection(deviceId: String, connection: PeerToPeerConnectionService) {
        scope.launch {
            connections[deviceId] = connection
            connectionTimes[deviceId] = Clock.System.now().toEpochMilliseconds()

            connection.connectionStatus.collect { updateStatus(deviceId, it) }
        }
    }

    override fun connectionStatusFlow(deviceId: String): Flow<ConnectionStatus> {
        return _statusFlow
            .map { it[deviceId] ?: ConnectionStatus.DISCONNECTED }
            .distinctUntilChanged()
    }

    override fun connectionStatus(deviceId: String): ConnectionStatus? {
        return _statusFlow.value[deviceId]
    }

    override fun hasOutgoingOffer(deviceId: String): Boolean {
        return connections[deviceId] != null
    }

    @OptIn(ExperimentalTime::class)
    override fun hasOutgoingOfferWithTimeout(deviceId: String): Boolean {
        connections[deviceId] ?: return false
        val createdAt = connectionTimes[deviceId] ?: return false
        val now = Clock.System.now().toEpochMilliseconds()

        val status = _statusFlow.value[deviceId]
        if (status != ConnectionStatus.CONNECTED && now - createdAt > 30_000L) {
            removeConnection(deviceId)
            return false
        }

        return true
    }

    override fun getConnection(deviceId: String): PeerToPeerConnectionService? =
        connections[deviceId]

    override fun removeConnection(deviceId: String) {
        connections[deviceId]?.close()
        connections.remove(deviceId)
        connectionTimes.remove(deviceId)
        _statusFlow.update { it - deviceId }
    }

    override fun allConnections(): List<PeerToPeerConnectionService> = connections.values.toList()
    override fun allConnectionDevices(): List<String> = connections.keys.toList()

    private fun updateStatus(deviceId: String, status: ConnectionStatus) {
        _statusFlow.value = _statusFlow.value + (deviceId to status)
    }

    override fun close() {
        connections.values.forEach { it.close() }
        connectionTimes.clear()
        scope.cancel()
    }
}