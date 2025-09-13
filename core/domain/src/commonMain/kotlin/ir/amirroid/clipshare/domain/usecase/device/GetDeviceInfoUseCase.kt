package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class GetDeviceInfoUseCase(
    private val devicesRepository: DevicesRepository
) {
    operator fun invoke() = devicesRepository.getDeviceInfo()
}