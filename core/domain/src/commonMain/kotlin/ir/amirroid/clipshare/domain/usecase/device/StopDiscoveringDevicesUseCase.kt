package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class StopDiscoveringDevicesUseCase(
    private val devicesRepository: DevicesRepository
) {
    suspend operator fun invoke() = devicesRepository.startDiscoveringNearbyDevices()
}