package ir.amirroid.clipshare.domain.repository.clipboard

import ir.amirroid.clipshare.domain.models.clipboard.ClipboardContentDomain
import kotlinx.coroutines.flow.Flow

interface ClipboardRepository {
    fun getHistory(): Flow<List<ClipboardContentDomain>>
    suspend fun setClipboardContent(entityId: Long)
    suspend fun setFileClipboardContent(file: String)
    suspend fun deleteEntity(id: Long)
    suspend fun deleteHistory()
}