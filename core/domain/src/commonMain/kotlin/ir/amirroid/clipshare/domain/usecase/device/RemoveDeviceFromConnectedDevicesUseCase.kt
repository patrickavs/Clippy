package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.models.device.Device
import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class RemoveDeviceFromConnectedDevicesUseCase(
    private val devicesRepository: DevicesRepository
) {
    suspend operator fun invoke(device: Device) = devicesRepository.disconnectDevice(device)
}