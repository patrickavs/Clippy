package ir.amirroid.clipshare.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.amirroid.clipshare.domain.usecase.clipboard.DeleteClipboardHistoryUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.DeleteClipboardItemUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.GetClipboardHistoryUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.SetClipboardContentUseCase
import ir.amirroid.clipshare.ui_models.clipboard.toUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClipboardHistoryViewModel(
    getClipboardHistoryUseCase: GetClipboardHistoryUseCase,
    private val setClipboardContentUseCase: SetClipboardContentUseCase,
    private val dispatcher: CoroutineDispatcher,
    private val deleteClipboardHistoryUseCase: DeleteClipboardHistoryUseCase,
    private val deleteClipboardItemUseCase: DeleteClipboardItemUseCase
) : ViewModel() {
    val history = getClipboardHistoryUseCase()
        .map { domains -> domains.map { it.toUiModel() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var showDeleteDialog by mutableStateOf(false)

    fun setClipboardPrimaryContent(id: Long) = viewModelScope.launch(dispatcher) {
        setClipboardContentUseCase.invoke(id)
    }

    fun clearAll() = viewModelScope.launch(dispatcher) {
        deleteClipboardHistoryUseCase.invoke()
    }

    fun deleteContent(id: Long) = viewModelScope.launch(dispatcher) {
        deleteClipboardItemUseCase.invoke(id)
    }
}