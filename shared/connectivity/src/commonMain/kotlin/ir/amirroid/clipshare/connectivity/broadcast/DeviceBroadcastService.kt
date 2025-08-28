package ir.amirroid.clipshare.connectivity.broadcast

interface DeviceBroadcastService {
    suspend fun startBroadcasting()
    suspend fun stopBroadcasting()

    companion object {
        internal const val BROADCAST_INTERVAL_MS = 3000L
    }
}