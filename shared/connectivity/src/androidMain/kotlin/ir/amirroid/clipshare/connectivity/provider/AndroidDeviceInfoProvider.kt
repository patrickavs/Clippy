package ir.amirroid.clipshare.connectivity.provider

import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.DeviceInfo
import ir.amirroid.clipshare.connectivity.utils.getDeviceName
import ir.amirroid.clipshare.database.entity.DevicePlatform

class AndroidDeviceInfoProvider(private val deviceUidProvider: DeviceUidProvider) :
    DeviceInfoProvider {
    override fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            name = getDeviceName(),
            platform = DevicePlatform.ANDROID,
            id = deviceUidProvider.getDeviceId()
        )
    }
}