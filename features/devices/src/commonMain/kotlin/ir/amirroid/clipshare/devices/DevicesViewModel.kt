package ir.amirroid.clipshare.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
import ir.amirroid.clipshare.connectivity.sync.SyncService
import ir.amirroid.clipshare.domain.usecase.device.GetIsStartedBroadcastingUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetNearByDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StartBroadcastingDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StartDiscoveringDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StopBroadcastingDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StopDiscoveringDevicesUseCase
import ir.amirroid.clipshare.ui_models.device.DiscoveredDeviceUiModel
import ir.amirroid.clipshare.ui_models.device.toUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val getNearByDevicesUseCase: GetNearByDevicesUseCase,
    private val getIsStartedBroadcastingUseCase: GetIsStartedBroadcastingUseCase,
    private val startDiscoveringDevicesUseCase: StartDiscoveringDevicesUseCase,
    private val stopDiscoveringDevicesUseCase: StopDiscoveringDevicesUseCase,
    private val startBroadcastingDevicesUseCase: StartBroadcastingDevicesUseCase,
    private val stopBroadcastingDevicesUseCase: StopBroadcastingDevicesUseCase,
    private val connectionRegistry: ConnectionRegistry,
    private val syncService: SyncService,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _screenState = MutableStateFlow(DevicesScreenState())
    val screenState = _screenState.asStateFlow()

    val connectedDevices = connectionRegistry.allConnectionStatus

    init {
        collectNearbyDevices()
        collectBroadcasting()
        viewModelScope.launch(dispatcher) {
            syncService.start()
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

    fun connectToDevice(device: DiscoveredDeviceUiModel) = viewModelScope.launch(dispatcher) {
        syncService.call(device.id)
    }
}