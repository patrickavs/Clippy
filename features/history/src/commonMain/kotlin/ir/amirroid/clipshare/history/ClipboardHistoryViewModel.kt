package ir.amirroid.clipshare.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.amirroid.clipshare.domain.usecase.clipboard.GetClipboardHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ClipboardHistoryViewModel(
    getClipboardHistoryUseCase: GetClipboardHistoryUseCase
) : ViewModel() {
    val history = getClipboardHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}