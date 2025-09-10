package ir.amirroid.clipshare.ui_models.device

import androidx.compose.runtime.Immutable
import ir.amirroid.clipshare.domain.models.utils.DevicePlatform

@Immutable
data class DeviceUiModel(
    val name: String,
    val id: String,
    val platform: DevicePlatform
)