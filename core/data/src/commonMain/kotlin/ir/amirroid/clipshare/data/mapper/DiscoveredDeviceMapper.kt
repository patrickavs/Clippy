package ir.amirroid.clipshare.data.mapper

import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice
import ir.amirroid.clipshare.domain.models.DevicePlatform
import ir.amirroid.clipshare.domain.models.DiscoveredDeviceDomain

fun DiscoveredDevice.toDomain() = DiscoveredDeviceDomain(
    name = name,
    id = deviceId,
    platform = DevicePlatform.valueOf(platform.name)
)