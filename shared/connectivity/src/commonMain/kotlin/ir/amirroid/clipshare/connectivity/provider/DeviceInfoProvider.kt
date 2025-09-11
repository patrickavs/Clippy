package ir.amirroid.clipshare.connectivity.provider

import ir.amirroid.clipshare.connectivity.models.DeviceInfo

interface DeviceInfoProvider {
    fun getDeviceInfo(): DeviceInfo
}