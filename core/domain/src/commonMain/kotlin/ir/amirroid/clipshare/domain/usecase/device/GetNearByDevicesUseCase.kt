package ir.amirroid.clipshare.domain.usecase.device

import ir.amirroid.clipshare.domain.repository.devices.DevicesRepository

class GetNearByDevicesUseCase(
    private val devicesRepository: DevicesRepository
) {
    operator fun invoke() = devicesRepository.nearbyDevices
}