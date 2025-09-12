package ir.amirroid.clipshare.database.dao.clipboard

import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.entity.ClipboardType
import kotlinx.coroutines.flow.Flow

interface ClipboardDao {
    fun getAllEntities(): Flow<List<ClipboardEntity>>
    suspend fun getEntityById(id: Long): ClipboardEntity
    suspend fun insert(type: ClipboardType, data: String, originDeviceId: String? = null): Long
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}