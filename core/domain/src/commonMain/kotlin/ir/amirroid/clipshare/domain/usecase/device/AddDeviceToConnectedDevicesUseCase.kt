package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.models.device.Device
import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class AddDeviceToConnectedDevicesUseCase(
    private val devicesRepository: DevicesRepository
) {
    suspend operator fun invoke(device: Device) = devicesRepository.connectToDevice(device)
}