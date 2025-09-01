package ir.amirroid.clipshare.ui_models.device

import ir.amirroid.clipshare.domain.models.DiscoveredDeviceDomain

fun DiscoveredDeviceDomain.toUiModel() = DiscoveredDeviceUiModel(
    name = name,
    id = id,
    platform = platform
)