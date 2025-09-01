package ir.amirroid.clipshare.domain.models


data class DiscoveredDeviceDomain(
    val name: String,
    val id: String,
    val platform: DevicePlatform
)