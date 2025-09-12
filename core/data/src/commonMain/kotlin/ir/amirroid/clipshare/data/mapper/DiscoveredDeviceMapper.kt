package ir.amirroid.clipshare.data.mapper

import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice
import ir.amirroid.clipshare.domain.models.utils.DevicePlatform
import ir.amirroid.clipshare.domain.models.device.Device

fun DiscoveredDevice.toDomain() = Device(
    name = name,
    id = deviceId,
    platform = DevicePlatform.valueOf(platform.name),
    isHost = true
)