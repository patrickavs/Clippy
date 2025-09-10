package ir.amirroid.clipshare.ui_models.connected_device

import androidx.compose.runtime.Immutable
import ir.amirroid.clipshare.domain.models.utils.ConnectionStatus
import ir.amirroid.clipshare.ui_models.device.DeviceUiModel


@Immutable
data class ConnectedDeviceUiModel(
    val connectionStatus: ConnectionStatus,
    val device: DeviceUiModel
)