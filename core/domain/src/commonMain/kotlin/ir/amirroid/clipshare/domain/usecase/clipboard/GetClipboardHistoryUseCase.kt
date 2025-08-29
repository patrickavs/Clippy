package ir.amirroid.clipshare.domain.usecase.clipboard

import ir.amirroid.clipshare.domain.repository.clipboard.ClipboardRepository

class GetClipboardHistoryUseCase(private val clipboardRepository: ClipboardRepository) {
    operator fun invoke() = clipboardRepository.getHistory()
}