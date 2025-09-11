package ir.amirroid.clipshare.connectivity.models

import ir.amirroid.clipshare.database.entity.DevicePlatform
import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val name: String,
    val platform: DevicePlatform,
    val id: String
)