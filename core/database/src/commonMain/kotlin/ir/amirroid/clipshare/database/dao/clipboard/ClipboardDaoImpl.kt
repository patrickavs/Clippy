package ir.amirroid.clipshare.database.dao.clipboard

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ir.amirroid.clipshare.database.ClipboardQueries
import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.entity.ClipboardType
import ir.amirroid.clipshare.database.mapper.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClipboardDaoImpl(
    private val clipboardQueries: ClipboardQueries,
    private val dispatcher: CoroutineDispatcher
) : ClipboardDao {
    override fun getAllEntities(): Flow<List<ClipboardEntity>> {
        return clipboardQueries.getAllEntries()
            .asFlow().mapToList(dispatcher).map { entries ->
                entries.map { it.toEntity() }
            }
    }

    override suspend fun getEntityById(id: Long): ClipboardEntity {
        return clipboardQueries.getById(id).executeAsOne().toEntity()
    }

    override suspend fun insert(type: ClipboardType, data: String, originDeviceId: String?): Long {
        return clipboardQueries.insertEntry(type.name, data, originDeviceId).await()
    }

    override suspend fun delete(id: Long) {
        clipboardQueries.deleteById(id).await()
    }

    override suspend fun deleteAll() {
        clipboardQueries.deleteAll().await()
    }
}