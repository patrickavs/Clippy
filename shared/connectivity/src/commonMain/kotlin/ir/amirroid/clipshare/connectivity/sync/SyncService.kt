package ir.amirroid.clipshare.connectivity.sync

interface SyncService {
    var isStarted: Boolean
    fun start()
    suspend fun call(targetDeviceId: String)
    suspend fun acceptConnection(targetDeviceId: String)
    suspend fun rejectConnection(targetDeviceId: String)
    fun close()
}