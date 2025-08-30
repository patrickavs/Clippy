package ir.amirroid.clipshare.data.repository.clipboard

import ir.amirroid.clipshare.clipboard.manager.ClipboardManager
import ir.amirroid.clipshare.clipboard.models.ClipboardContentRequest
import ir.amirroid.clipshare.clipboard.models.ClipboardContentType
import ir.amirroid.clipshare.data.mapper.toDomain
import ir.amirroid.clipshare.database.dao.ClipboardDao
import ir.amirroid.clipshare.domain.models.ClipboardContentDomain
import ir.amirroid.clipshare.domain.repository.clipboard.ClipboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ClipboardRepositoryImpl(
    private val clipboardManager: ClipboardManager,
    private val clipboardDao: ClipboardDao,
    private val json: Json
) : ClipboardRepository {
    override fun getHistory(): Flow<List<ClipboardContentDomain>> {
        return clipboardDao.getAllEntities().map { entities ->
            entities.map { it.toDomain(json) }
        }
    }

    override suspend fun setClipboardContent(entityId: Long) {
        val entity = clipboardDao.getEntityById(entityId)
        val type = ClipboardContentType.valueOf(entity.type.name)
        val request = ClipboardContentRequest(
            type = type,
            data = entity.data
        )
        clipboardManager.setContent(request)
    }
}