package ir.amirroid.clipshare.database.dao

import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.entity.ClipboardType
import kotlinx.coroutines.flow.Flow

interface ClipboardDao {
    fun getAllEntities(): Flow<List<ClipboardEntity>>
    suspend fun insert(type: ClipboardType, data: String)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}