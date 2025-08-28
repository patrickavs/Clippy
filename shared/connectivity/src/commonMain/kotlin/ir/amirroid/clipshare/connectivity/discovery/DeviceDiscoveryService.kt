package ir.amirroid.clipshare.connectivity.discovery

import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice
import kotlinx.coroutines.flow.Flow

interface DeviceDiscoveryService {
    val incoming: Flow<DiscoveredDevice>

    suspend fun startDiscovery()
    suspend fun stopDiscovery()
}