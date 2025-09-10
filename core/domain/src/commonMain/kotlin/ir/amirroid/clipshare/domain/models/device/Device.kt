package ir.amirroid.clipshare.domain.models.device

import ir.amirroid.clipshare.domain.models.utils.DevicePlatform


data class Device(
    val name: String,
    val id: String,
    val platform: DevicePlatform
)