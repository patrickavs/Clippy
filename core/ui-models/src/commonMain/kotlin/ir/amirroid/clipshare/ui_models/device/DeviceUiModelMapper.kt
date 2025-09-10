package ir.amirroid.clipshare.ui_models.device

import ir.amirroid.clipshare.domain.models.device.Device

fun DeviceUiModel.toDomain() = Device(
    name = name,
    id = id,
    platform = platform
)