package ir.amirroid.clipshare.connectivity.sync

interface SyncService {
    suspend fun start()
    suspend fun call(targetDeviceId: String)
    fun close()
}