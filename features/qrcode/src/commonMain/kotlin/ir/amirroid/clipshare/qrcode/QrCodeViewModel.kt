package ir.amirroid.clipshare.qrcode

import androidx.lifecycle.ViewModel
import ir.amirroid.clipshare.domain.usecase.device.GetDeviceInfoUseCase
import kotlinx.serialization.json.Json

class QrCodeViewModel(
    getDeviceInfoUseCase: GetDeviceInfoUseCase,
    json: Json
) : ViewModel() {
    val deviceInfoJson = json.encodeToString(getDeviceInfoUseCase())
}