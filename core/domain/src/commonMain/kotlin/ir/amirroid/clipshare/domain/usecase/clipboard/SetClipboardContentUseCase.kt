package ir.amirroid.clipshare.domain.usecase.clipboard

import ir.amirroid.clipshare.domain.repository.clipboard.ClipboardRepository

class SetClipboardContentUseCase(
    private val clipboardRepository: ClipboardRepository
) {
    suspend operator fun invoke(id: Long) =
        clipboardRepository.setClipboardContent(id)
}