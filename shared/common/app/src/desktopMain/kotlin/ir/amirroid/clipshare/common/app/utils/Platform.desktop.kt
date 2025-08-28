package ir.amirroid.clipshare.common.app.utils

internal actual fun getCurrentPlatform(): Platform {
    val osName = System.getProperty("os.name").lowercase()

    return when {
        osName.contains("mac") -> Platform.Desktop.Mac
        osName.contains("win") -> Platform.Desktop.Windows
        osName.contains("nux") || osName.contains("nix") -> Platform.Desktop.Linux
        else -> throw IllegalStateException("Unsupported desktop platform: $osName")
    }
}