package ir.amirroid.clipshare.connectivity.device

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

class AndroidDeviceUidProviderImpl(
    private val context: Context
) : DeviceUidProvider {
    @Volatile
    private var deviceId: String? = null

    @SuppressLint("HardwareIds")
    override fun getDeviceId(): String = synchronized(this) {
        deviceId ?: Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            .also { deviceId = it.orEmpty() }
            .orEmpty()
    }
}