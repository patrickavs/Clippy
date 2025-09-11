package ir.amirroid.clipshare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.amirroid.clipshare.domain.usecase.device.AcceptPendingConnectionUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetPendingConnectionsUseCase
import ir.amirroid.clipshare.domain.usecase.device.RejectPendingConnectionUseCase
import ir.amirroid.clipshare.ui_models.device.toUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    getPendingConnectionsUseCase: GetPendingConnectionsUseCase,
    private val acceptPendingConnectionUseCase: AcceptPendingConnectionUseCase,
    private val rejectPendingConnectionUseCase: RejectPendingConnectionUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    val pendingConnections = getPendingConnectionsUseCase.invoke()
        .map { connections -> connections.map { it.toUiModel() }.toImmutableList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, persistentListOf())

    fun accept(deviceId: String) = viewModelScope.launch(dispatcher) {
        acceptPendingConnectionUseCase.invoke(deviceId)
    }

    fun reject(deviceId: String) = viewModelScope.launch(dispatcher) {
        rejectPendingConnectionUseCase.invoke(deviceId)
    }
}