package ir.amirroid.clipshare.database.entity

enum class DevicePlatform {
    ANDROID, IOS, DESKTOP
}

data class DeviceEntity(
    val name: String,
    val id: String,
    val platform: DevicePlatform
)
