package ir.amirroid.clipshare.database.dao.sync_status

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ir.amirroid.clipshare.database.Clipboard_sync_statusQueries
import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.mapper.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SyncStatusDaoImpl(
    private val clipboardSyncStatusQueries: Clipboard_sync_statusQueries,
    private val dispatcher: CoroutineDispatcher
) : SyncStatusDao {
    override fun getAllUnsyncedClipboardItems(deviceId: String): Flow<List<ClipboardEntity>> {
        return clipboardSyncStatusQueries.getUnsyncedEntriesForDevice(deviceId, deviceId).asFlow()
            .mapToList(dispatcher).map { clipboard ->
                clipboard.map { item ->
                    item.toEntity()
                }
            }
    }

    override fun insertStatus(deviceId: String, clipboardId: Long) {
        clipboardSyncStatusQueries.insertSyncStatus(clipboardId, deviceId)
    }

    override suspend fun markAsSynced(
        devicesId: String,
        clipboardIds: List<Long>
    ) {
        clipboardSyncStatusQueries.transaction {
            clipboardIds.forEach { clipboardId ->
                clipboardSyncStatusQueries.insertSyncStatus(clipboardId, devicesId)
            }
        }
    }
}