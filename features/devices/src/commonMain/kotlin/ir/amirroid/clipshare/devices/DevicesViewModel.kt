package ir.amirroid.clipshare.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.amirroid.clipshare.domain.usecase.device.AddDeviceToConnectedDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetConnectedDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetIsStartedBroadcastingUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetNearByDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.RemoveDeviceFromConnectedDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StartBroadcastingDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StartDiscoveringDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StopBroadcastingDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StopDiscoveringDevicesUseCase
import ir.amirroid.clipshare.ui_models.connected_device.toUiModel
import ir.amirroid.clipshare.ui_models.device.DeviceUiModel
import ir.amirroid.clipshare.ui_models.device.toDomain
import ir.amirroid.clipshare.ui_models.device.toUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val getNearByDevicesUseCase: GetNearByDevicesUseCase,
    private val getIsStartedBroadcastingUseCase: GetIsStartedBroadcastingUseCase,
    private val startDiscoveringDevicesUseCase: StartDiscoveringDevicesUseCase,
    private val stopDiscoveringDevicesUseCase: StopDiscoveringDevicesUseCase,
    private val startBroadcastingDevicesUseCase: StartBroadcastingDevicesUseCase,
    private val stopBroadcastingDevicesUseCase: StopBroadcastingDevicesUseCase,
    private val addDeviceToConnectedDevicesUseCase: AddDeviceToConnectedDevicesUseCase,
    private val removeDeviceFromConnectedDevicesUseCase: RemoveDeviceFromConnectedDevicesUseCase,
    private val getConnectedDevicesUseCase: GetConnectedDevicesUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _screenState = MutableStateFlow(DevicesScreenState())
    val screenState = _screenState.map { state ->
        val connectedDevicesIds = state.connectedDevices.map { it.device.id }
        state.copy(
            nearbyDevices = state.nearbyDevices.filter { it.id !in connectedDevicesIds }
                .toImmutableList()
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DevicesScreenState())

    init {
        collectNearbyDevices()
        collectBroadcasting()
        collectConnectedDevices()
    }

    private fun collectConnectedDevices() = viewModelScope.launch(dispatcher) {
        getConnectedDevicesUseCase.invoke()
            .map { devices -> devices.map { it.toUiModel() }.toImmutableList() }
            .collectLatest { devices ->
                _screenState.update { it.copy(connectedDevices = devices) }
            }
    }


    private fun collectNearbyDevices() = viewModelScope.launch(dispatcher) {
        getNearByDevicesUseCase.invoke()
            .map { devices -> devices.map { it.toUiModel() }.toImmutableList() }
            .collectLatest { devices ->
                _screenState.update { it.copy(nearbyDevices = devices) }
            }
    }

    private fun collectBroadcasting() = viewModelScope.launch(dispatcher) {
        getIsStartedBroadcastingUseCase.invoke()
            .collectLatest { isBroadcasting ->
                _screenState.update { it.copy(isBroadcasting = isBroadcasting) }
            }
    }

    fun startDiscovering() = viewModelScope.launch(dispatcher) {
        startDiscoveringDevicesUseCase.invoke()
    }

    fun stopDiscovering() = viewModelScope.launch(dispatcher) {
        stopDiscoveringDevicesUseCase.invoke()
    }

    fun startBroadcasting() = viewModelScope.launch(dispatcher) {
        startBroadcastingDevicesUseCase.invoke()
    }

    fun stopBroadcasting() = viewModelScope.launch(dispatcher) {
        stopBroadcastingDevicesUseCase.invoke()
    }

    fun connectToDevice(device: DeviceUiModel) = viewModelScope.launch(dispatcher) {
        addDeviceToConnectedDevicesUseCase.invoke(device.toDomain())
    }
}