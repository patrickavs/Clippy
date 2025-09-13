package ir.amirroid.clipshare.domain.models.device

import ir.amirroid.clipshare.domain.models.utils.DevicePlatform
import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val name: String,
    val id: String,
    val platform: DevicePlatform,
    val isHost: Boolean,
)