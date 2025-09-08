package ir.amirroid.clipshare.connectivity.sync

import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import kotlinx.coroutines.flow.Flow

interface SyncService {
    val connectionStatus: Flow<ConnectionStatus>
    suspend fun start()
    suspend fun call(targetDeviceId: String)
    fun close()
}