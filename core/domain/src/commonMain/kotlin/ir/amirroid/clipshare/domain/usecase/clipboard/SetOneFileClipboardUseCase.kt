package ir.amirroid.clipshare.domain.usecase.clipboard

import ir.amirroid.clipshare.domain.repository.clipboard.ClipboardRepository

class SetOneFileClipboardUseCase(
    private val repository: ClipboardRepository
) {
    suspend operator fun invoke(file: String) = repository.setFileClipboardContent(file)
}