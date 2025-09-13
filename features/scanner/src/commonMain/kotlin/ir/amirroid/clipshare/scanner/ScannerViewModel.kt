package ir.amirroid.clipshare.scanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.amirroid.clipshare.common.app.events.EventBus
import ir.amirroid.clipshare.common.app.models.NotificationRequest
import ir.amirroid.clipshare.domain.models.device.Device
import ir.amirroid.clipshare.domain.usecase.device.AddDeviceToConnectedDevicesUseCase
import ir.amirroid.clipshare.ui_models.device.DeviceUiModel
import ir.amirroid.clipshare.ui_models.device.toDomain
import ir.amirroid.clipshare.ui_models.device.toUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ScannerViewModel(
    private val addDeviceToConnectedDevicesUseCase: AddDeviceToConnectedDevicesUseCase,
    private val json: Json,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    var foundDevice: DeviceUiModel? by mutableStateOf(null)

    fun parseDevice(deviceJson: String) {
        foundDevice = runCatching {
            json.decodeFromString<Device>(deviceJson)
        }.onFailure {
            viewModelScope.launch(dispatcher) {
                EventBus.publish(
                    NotificationRequest(
                        title = "Invalid QR Code",
                        description = "The scanned QR code does not belong to this app."
                    )
                )
            }
        }.getOrNull()?.toUiModel()
    }

    fun connectToDevice(device: DeviceUiModel) = viewModelScope.launch(dispatcher) {
        addDeviceToConnectedDevicesUseCase.invoke(device.toDomain())
    }
}