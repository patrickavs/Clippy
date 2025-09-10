package ir.amirroid.clipshare.database.mapper

import ir.amirroid.clipshare.database.DiscoveredDeviceEntries
import ir.amirroid.clipshare.database.entity.DeviceEntity
import ir.amirroid.clipshare.database.entity.DevicePlatform

fun DiscoveredDeviceEntries.toEntity() = DeviceEntity(
    id = deviceId,
    platform = DevicePlatform.valueOf(platform),
    name = name
)