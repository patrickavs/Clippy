package ir.amirroid.clipshare.devices

import androidx.compose.runtime.Immutable
import ir.amirroid.clipshare.ui_models.connected_device.ConnectedDeviceUiModel
import ir.amirroid.clipshare.ui_models.device.DeviceUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DevicesScreenState(
    val nearbyDevices: ImmutableList<DeviceUiModel> = persistentListOf(),
    val connectedDevices: ImmutableList<ConnectedDeviceUiModel> = persistentListOf(),
    val isBroadcasting: Boolean = true
)