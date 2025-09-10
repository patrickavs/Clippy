package ir.amirroid.clipshare.connectivity.sync

interface SyncService {
    var isStarted: Boolean
    fun start()
    suspend fun call(targetDeviceId: String)
    fun close()
}