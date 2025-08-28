package ir.amirroid.clipshare.connectivity.discovery

import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice
import kotlinx.coroutines.flow.StateFlow

interface DeviceDiscoveryService {
    val incoming: StateFlow<List<DiscoveredDevice>>
    val isStarted: StateFlow<Boolean>

    suspend fun startDiscovery()
    suspend fun stopDiscovery()


    companion object {
        internal const val BUFFER_SIZE = 1024
    }
}