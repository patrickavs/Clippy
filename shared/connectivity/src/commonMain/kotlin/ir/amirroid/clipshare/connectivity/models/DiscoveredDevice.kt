package ir.amirroid.clipshare.connectivity.models

enum class DiscoveredPlatform {
    IOS, ANDROID
}

data class DiscoveredDevice(
    val ip: String,
    val port: Int,
    val deviceId: String,
    val name: String,
    val platform: DiscoveredPlatform,
    val lastSeen: Long
)