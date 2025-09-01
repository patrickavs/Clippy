package ir.amirroid.clipshare.ui_models.device

import androidx.compose.runtime.Immutable
import ir.amirroid.clipshare.domain.models.DevicePlatform

@Immutable
data class DiscoveredDeviceUiModel(
    val name: String,
    val id: String,
    val platform: DevicePlatform
)