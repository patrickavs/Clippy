package ir.amirroid.clipshare.database.dao.sync_status

import ir.amirroid.clipshare.database.entity.ClipboardEntity
import kotlinx.coroutines.flow.Flow

interface SyncStatusDao {
    fun getAllUnsyncedClipboardItems(deviceId: String): Flow<List<ClipboardEntity>>
    fun insertStatus(deviceId: String, clipboardId: Long)
    suspend fun markAsSynced(devicesId: String, clipboardIds: List<Long>)
}