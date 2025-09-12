package ir.amirroid.clipshare.data.mapper

import ir.amirroid.clipshare.database.entity.DeviceEntity
import ir.amirroid.clipshare.database.entity.DevicePlatform
import ir.amirroid.clipshare.domain.models.device.Device

internal fun Device.toEntity() = DeviceEntity(
    name = name,
    id = id,
    platform = DevicePlatform.valueOf(platform.name),
    isHost = isHost
)