package ir.amirroid.clipshare.common.app.utils

sealed interface Platform {
    sealed interface Desktop : Platform {
        data object Mac : Desktop
        data object Windows : Desktop
        data object Linux : Desktop
    }

    sealed interface Mobile : Platform {
        data object Android : Mobile
        data object Ios : Mobile
    }

    companion object : Platform by getCurrentPlatform() {
        fun current() = getCurrentPlatform()
    }
}


internal expect fun getCurrentPlatform(): Platform


fun Platform.isMac(): Boolean = this is Platform.Desktop.Mac
fun Platform.isWindows(): Boolean = this is Platform.Desktop.Windows
fun Platform.isLinux(): Boolean = this is Platform.Desktop.Linux

fun Platform.isAndroid(): Boolean = this is Platform.Mobile.Android
fun Platform.isIos(): Boolean = this is Platform.Mobile.Ios

fun Platform.isDesktop(): Boolean = this is Platform.Desktop
fun Platform.isMobile(): Boolean = this is Platform.Mobile