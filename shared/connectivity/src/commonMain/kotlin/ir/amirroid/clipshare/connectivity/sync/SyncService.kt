package ir.amirroid.clipshare.connectivity.sync

import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice

interface SyncService {
    fun connect(device: DiscoveredDevice)
}