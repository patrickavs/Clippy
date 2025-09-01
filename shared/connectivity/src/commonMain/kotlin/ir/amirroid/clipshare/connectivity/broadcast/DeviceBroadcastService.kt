package ir.amirroid.clipshare.connectivity.broadcast

import kotlinx.coroutines.flow.StateFlow

interface DeviceBroadcastService {
    val isStarted: StateFlow<Boolean>

    suspend fun startBroadcasting()
    suspend fun stopBroadcasting()

    companion object {
        internal const val BROADCAST_INTERVAL_MS = 3000L
    }
}