package ir.amirroid.clipshare.domain.repository.clipboard

import ir.amirroid.clipshare.domain.models.ClipboardContentDomain
import kotlinx.coroutines.flow.Flow

interface ClipboardRepository {
    fun getHistory(): Flow<List<ClipboardContentDomain>>
    suspend fun setClipboardContent(entityId: Long)
    suspend fun deleteEntity(id: Long)
    suspend fun deleteHistory()
}