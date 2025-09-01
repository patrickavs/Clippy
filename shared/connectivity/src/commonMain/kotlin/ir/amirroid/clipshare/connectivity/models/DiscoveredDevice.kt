package ir.amirroid.clipshare.connectivity.models

import kotlinx.serialization.Serializable

enum class DiscoveredPlatform {
    IOS, ANDROID, DESKTOP
}

enum class RequestType {
    ADD, REMOVE
}

@Serializable
data class DiscoveredDevice(
    val deviceId: String,
    val name: String,
    val platform: DiscoveredPlatform,
    val requestType: RequestType,
    val ip: String = ""
)