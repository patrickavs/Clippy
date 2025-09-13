package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class RemoveDeviceFromConnectedDevicesUseCase(
    private val devicesRepository: DevicesRepository
) {
    suspend operator fun invoke(deviceId: String) = devicesRepository.disconnectDevice(deviceId)
}