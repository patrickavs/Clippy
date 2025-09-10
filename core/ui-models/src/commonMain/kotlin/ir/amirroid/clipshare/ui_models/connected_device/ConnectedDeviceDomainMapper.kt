package ir.amirroid.clipshare.ui_models.connected_device

import ir.amirroid.clipshare.domain.models.device.ConnectedDevice
import ir.amirroid.clipshare.ui_models.device.toUiModel

fun ConnectedDevice.toUiModel() = ConnectedDeviceUiModel(
    connectionStatus = connectionStatus,
    device = device.toUiModel()
)