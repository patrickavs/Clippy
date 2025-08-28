package ir.amirroid.clipshare.connectivity.models

import kotlinx.serialization.Serializable

enum class DiscoveredPlatform {
    IOS, ANDROID, DESKTOP
}

@Serializable
data class DiscoveredDevice(
    val deviceId: String,
    val name: String,
    val platform: DiscoveredPlatform,
    val ip: String = ""
)