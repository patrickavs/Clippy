package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class StartDiscoveringDevicesUseCase(
    private val devicesRepository: DevicesRepository
) {
    suspend operator fun invoke() = devicesRepository.startDiscoveringNearbyDevices()
}