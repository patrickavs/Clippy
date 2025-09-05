package ir.amirroid.clipshare.connectivity.sync

import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice

interface SyncService {
    suspend fun start()
    suspend fun call(target: DiscoveredDevice)
    fun close()
}