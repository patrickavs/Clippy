package ir.amirroid.clipshare.ui_models.device

import ir.amirroid.clipshare.domain.models.device.Device

fun Device.toUiModel() = DeviceUiModel(
    name = name,
    id = id,
    platform = platform
)