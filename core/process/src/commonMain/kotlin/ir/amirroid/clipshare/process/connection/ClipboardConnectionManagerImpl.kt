package ir.amirroid.clipshare.process.connection

import co.touchlab.kermit.Logger
import ir.amirroid.clipshare.clipboard.manager.ClipboardManager
import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.models.ClipboardContentRequest
import ir.amirroid.clipshare.clipboard.models.ClipboardContentType
import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import ir.amirroid.clipshare.connectivity.signaling.SignalingServiceImpl
import ir.amirroid.clipshare.connectivity.sync.SyncService
import ir.amirroid.clipshare.database.dao.clipboard.ClipboardDao
import ir.amirroid.clipshare.database.dao.device.DeviceDao
import ir.amirroid.clipshare.database.dao.sync_status.SyncStatusDao
import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.entity.ClipboardType
import ir.amirroid.clipshare.process.models.FileBuffer
import ir.amirroid.clipshare.process.wrapper.Base64Wrapper
import ir.amirroid.clipshare.storage.PlatformStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ClipboardConnectionManagerImpl(
    private val deviceDao: DeviceDao,
    private val syncService: SyncService,
    private val signalingServiceImpl: SignalingServiceImpl,
    private val connectionRegistry: ConnectionRegistry,
    private val storage: PlatformStorage,
    private val json: Json,
    private val clipboardManager: ClipboardManager,
    private val syncStatusDao: SyncStatusDao,
    private val clipboardDao: ClipboardDao,
    private val dispatcher: CoroutineDispatcher
) : ClipboardConnectionManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun start() {
        if (syncService.isStarted.not()) syncService.start()
        signalingServiceImpl.onConnected {
            scope.launch {
                launch { handleDevicesFromDatabase() }
                launch { observeIncomingMessages() }
            }
        }
    }

    private suspend fun handleDevicesFromDatabase() {
        deviceDao.getAllDiscoveredDevices().collect { devices ->
            val currentAllConnectedDevices = connectionRegistry.allConnectionDevices()
            val devicesIds = devices.map { it.id }

            devices.filter { it.id !in currentAllConnectedDevices && it.isHost }.forEach {
                syncService.call(it.id)
            }
            currentAllConnectedDevices.filter { it !in devicesIds }.forEach {
                connectionRegistry.removeConnection(it)
            }
        }
    }

    private suspend fun observeIncomingMessages() {
        connectionRegistry.allConnectionStatus.collect { connectionStatuses ->
            connectionStatuses.forEach { (deviceId, status) ->
                if (status != ConnectionStatus.CONNECTED) return@collect
                connectionRegistry.getConnection(deviceId)?.also { connection ->
                    handleOnMessageReceived(deviceId, connection)
                    observeToUnsyncedClipboardItems(deviceId, connection)
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun observeToUnsyncedClipboardItems(
        deviceId: String,
        connection: PeerToPeerConnectionService
    ) {
        syncStatusDao.getAllUnsyncedClipboardItems(deviceId)
            .debounce(300)
            .collectLatest { entities ->
                if (entities.isEmpty()) return@collectLatest

                val successfulIds = entities.mapNotNull { entity ->
                    val request = createRequestFromEntity(entity) ?: return@mapNotNull null
                    runCatching {
                        connection.sendMessage(json.encodeToString(request))
                        entity.id
                    }.getOrNull()
                }
                if (successfulIds.isNotEmpty()) {
                    syncStatusDao.markAsSynced(deviceId, clipboardIds = successfulIds)
                }
            }
    }

    private suspend fun createRequestFromEntity(entity: ClipboardEntity): ClipboardContentRequest? {
        return when (entity.type) {
            ClipboardType.FILES -> {
                val paths = json.decodeFromString<List<String>>(entity.data)

                if (paths.isEmpty()) return null

                val buffers = paths.mapNotNull {
                    val info = storage.getFileInfo(it)
                    if (!info.exists || info.length > MAX_CHUNK_SIZE || info.isDirectory) return@mapNotNull null
                    FileBuffer(
                        fileName = info.name,
                        data = storage.readBytes(it)
                    )
                }
                ClipboardContentRequest(
                    type = ClipboardContentType.FILES,
                    data = json.encodeToString(buffers),
                )
            }

            ClipboardType.IMAGE -> {
                val info = storage.getFileInfo(entity.data)
                if (!info.exists || info.length > MAX_CHUNK_SIZE) return null
                ClipboardContentRequest(
                    type = ClipboardContentType.IMAGE,
                    data = Base64Wrapper.encodeToString(storage.readBytes(entity.data)),
                )
            }

            else -> {
                ClipboardContentRequest(
                    type = ClipboardContentType.valueOf(entity.type.name),
                    data = entity.data,
                )
            }
        }
    }

    private fun handleOnMessageReceived(deviceId: String, connection: PeerToPeerConnectionService) {
        connection.onMessageReceived { buffer ->
            if (buffer.binary) return@onMessageReceived

            scope.launch(dispatcher) {
                val request = buffer.data.decodeToString()
                    .let { json.decodeFromString<ClipboardContentRequest>(it) }
                    .let { processClipboardRequest(it) }

                clipboardDao.insert(
                    type = ClipboardType.valueOf(request.type.name),
                    data = request.data,
                    originDeviceId = deviceId
                )
                clipboardManager.setContent(request, withMessage = false, withSaveLastItem = true)
            }
        }
    }

    private suspend fun processClipboardRequest(request: ClipboardContentRequest): ClipboardContentRequest {
        return when (request.type) {
            ClipboardContentType.IMAGE -> request.copy(
                data = saveFile(Base64Wrapper.decodeToByteArray(request.data))
            )

            ClipboardContentType.FILES -> request.copy(
                data = processFiles(request.data)
            )

            else -> request
        }
    }

    private suspend fun processFiles(data: String): String {
        val fileBuffers = json.decodeFromString<List<FileBuffer>>(data)
        val savedFiles = fileBuffers.map {
            storage.saveToCacheWithFileName(it.data, it.fileName)
        }
        return json.encodeToString(savedFiles)
    }

    private suspend fun saveFile(bytes: ByteArray) = storage.saveToCache(
        bytes, ".png"
    )

    override fun close() {
        syncService.close()
        scope.cancel()
    }

    override fun handle(content: ClipboardContent) {
        // no-op
    }

    companion object {
        const val MAX_CHUNK_SIZE = 100 * 1024 // 100 KB
    }
}