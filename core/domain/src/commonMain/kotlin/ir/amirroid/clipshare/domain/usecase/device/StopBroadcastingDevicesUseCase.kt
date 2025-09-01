package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class StopBroadcastingDevicesUseCase(
    private val devicesRepository: DevicesRepository
) {
    suspend operator fun invoke() = devicesRepository.stopBroadcastingMyDevice()
}