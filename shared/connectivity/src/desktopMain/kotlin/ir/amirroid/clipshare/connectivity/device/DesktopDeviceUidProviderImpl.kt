package ir.amirroid.clipshare.connectivity.device

import ir.amirroid.clipshare.common.app.utils.CacheFiles.appCacheFolder
import java.io.File
import java.util.UUID

class DesktopDeviceUidProviderImpl : DeviceUidProvider {
    private val deviceIdFile: File
        get() = appCacheFolder.resolve(".deviceId")
    private var deviceId: String? = null

    override fun getDeviceId(): String = synchronized(this) {
        deviceId ?: run {
            val id = if (deviceIdFile.exists()) {
                deviceIdFile.readText()
            } else {
                val newId = UUID.randomUUID().toString()
                deviceIdFile.writeText(newId)
                newId
            }
            deviceId = id
            id
        }
    }
}