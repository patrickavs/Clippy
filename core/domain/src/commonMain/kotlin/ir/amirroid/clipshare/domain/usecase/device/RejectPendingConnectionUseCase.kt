package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class RejectPendingConnectionUseCase(
    private val devicesRepository: DevicesRepository
) {
    suspend operator fun invoke(deviceId: String) = devicesRepository.rejectPendingDevice(deviceId)
}