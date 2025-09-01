package ir.amirroid.clipshare.ui_models.device

import androidx.compose.runtime.Immutable

@Immutable
data class DiscoveredDeviceUiModel(
    val name: String,
    val id: String
)