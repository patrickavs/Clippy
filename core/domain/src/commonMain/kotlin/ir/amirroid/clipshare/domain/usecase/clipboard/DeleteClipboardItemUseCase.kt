package ir.amirroid.clipshare.domain.usecase.clipboard

import ir.amirroid.clipshare.domain.repository.clipboard.ClipboardRepository

class DeleteClipboardItemUseCase(
    private val clipboardRepository: ClipboardRepository
) {
    suspend operator fun invoke(id: Long) = clipboardRepository.deleteEntity(id)
}