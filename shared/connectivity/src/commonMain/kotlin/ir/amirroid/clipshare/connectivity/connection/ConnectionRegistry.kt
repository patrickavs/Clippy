package ir.amirroid.clipshare.connectivity.connection

import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import kotlinx.coroutines.flow.Flow

interface ConnectionRegistry {
    val allConnectionStatus: Flow<Map<String, ConnectionStatus>>
    fun addConnection(deviceId: String, connection: PeerToPeerConnectionService)
    fun getConnection(deviceId: String): PeerToPeerConnectionService?
    fun removeConnection(deviceId: String)
    fun allConnections(): List<PeerToPeerConnectionService>
    fun close()
}