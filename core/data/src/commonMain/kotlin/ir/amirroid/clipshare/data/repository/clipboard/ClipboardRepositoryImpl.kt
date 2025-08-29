package ir.amirroid.clipshare.data.repository.clipboard

import ir.amirroid.clipshare.data.mapper.toDomain
import ir.amirroid.clipshare.database.dao.ClipboardDao
import ir.amirroid.clipshare.domain.models.ClipboardContentDomain
import ir.amirroid.clipshare.domain.repository.clipboard.ClipboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ClipboardRepositoryImpl(
    private val clipboardDao: ClipboardDao,
    private val json: Json
) : ClipboardRepository {
    override fun getHistory(): Flow<List<ClipboardContentDomain>> {
        return clipboardDao.getAllEntities().map { entities ->
            entities.map { it.toDomain(json) }
        }
    }
}