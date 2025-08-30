package ir.amirroid.clipshare.domain.usecase.clipboard

import ir.amirroid.clipshare.domain.repository.clipboard.ClipboardRepository

class DeleteClipboardHistoryUseCase(
    private val clipboardRepository: ClipboardRepository
) {
    suspend operator fun invoke() = clipboardRepository.deleteHistory()
}