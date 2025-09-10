package ir.amirroid.clipshare.process.service

import ir.amirroid.clipshare.clipboard.manager.ClipboardManager
import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.database.dao.clipboard.ClipboardDao
import ir.amirroid.clipshare.database.entity.ClipboardType
import ir.amirroid.clipshare.process.connection.ConnectionManager
import ir.amirroid.clipshare.storage.PlatformStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ClipboardProcessorServiceImpl(
    private val clipboardManager: ClipboardManager,
    private val clipboardDao: ClipboardDao,
    private val storage: PlatformStorage,
    private val connectionManager: ConnectionManager,
    private val json: Json,
    dispatcher: CoroutineDispatcher
) : ClipboardProcessorService {
    val job = SupervisorJob()
    private val scope = CoroutineScope(dispatcher + job)

    override fun start() {
        clipboardManager.addOnChangedListener { content ->
            scope.launch {
                val request = createAddRequestFromContent(content)
                clipboardDao.insert(request.type, request.data)
            }
        }
        connectionManager.start()
    }

    override fun dispose() {
        clipboardManager.dispose()
        connectionManager.close()
        job.cancel()
    }

    private suspend fun createAddRequestFromContent(content: ClipboardContent): AddRequest {
        return when (content) {
            is ClipboardContent.Text -> AddRequest(content.value, ClipboardType.TEXT)
            is ClipboardContent.Files -> AddRequest(
                json.encodeToString(content.paths),
                ClipboardType.FILES
            )

            is ClipboardContent.Rtf -> AddRequest(
                content.content,
                ClipboardType.RTF
            )

            is ClipboardContent.Html -> AddRequest(
                json.encodeToString(content.data),
                ClipboardType.HTML
            )

            is ClipboardContent.Image -> AddRequest(
                storage.saveToCache(content.bytes, "png"),
                ClipboardType.IMAGE
            )
        }
    }

    private data class AddRequest(
        val data: String,
        val type: ClipboardType
    )
}