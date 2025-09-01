package ir.amirroid.clipshare.devices

import androidx.compose.runtime.Immutable
import ir.amirroid.clipshare.ui_models.device.DiscoveredDeviceUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DevicesScreenState(
    val nearbyDevices: ImmutableList<DiscoveredDeviceUiModel> = persistentListOf(),
    val isBroadcasting: Boolean = true
)