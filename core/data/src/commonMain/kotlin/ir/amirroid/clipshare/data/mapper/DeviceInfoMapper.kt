package ir.amirroid.clipshare.data.mapper

import ir.amirroid.clipshare.connectivity.models.DeviceInfo
import ir.amirroid.clipshare.domain.models.device.Device
import ir.amirroid.clipshare.domain.models.utils.DevicePlatform

fun DeviceInfo.toDomain() = Device(
    name = name,
    id = id,
    platform = DevicePlatform.valueOf(platform.name)
)