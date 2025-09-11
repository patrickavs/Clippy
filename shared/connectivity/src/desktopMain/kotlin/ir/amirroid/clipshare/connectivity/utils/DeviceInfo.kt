package ir.amirroid.clipshare.connectivity.utils

fun getDeviceName(): String {
    val osName = System.getProperty("os.name")
    val osVersion = System.getProperty("os.version")
    val userName = System.getProperty("user.name")

    return "$userName@$osName $osVersion"
}