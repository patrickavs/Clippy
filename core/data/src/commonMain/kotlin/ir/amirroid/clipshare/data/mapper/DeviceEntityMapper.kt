package ir.amirroid.clipshare.data.mapper

import ir.amirroid.clipshare.database.entity.DeviceEntity
import ir.amirroid.clipshare.domain.models.device.Device
import ir.amirroid.clipshare.domain.models.utils.DevicePlatform

fun DeviceEntity.toDomain() = Device(
    name = name,
    id = id,
    platform = DevicePlatform.valueOf(platform.name),
    isHost = isHost
)