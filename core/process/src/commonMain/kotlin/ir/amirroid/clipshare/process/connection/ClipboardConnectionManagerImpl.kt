package ir.amirroid.clipshare.process.connection

import co.touchlab.kermit.Logger
import ir.amirroid.clipshare.clipboard.manager.ClipboardManager
import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import ir.amirroid.clipshare.clipboard.models.ClipboardContentRequest
import ir.amirroid.clipshare.clipboard.models.ClipboardContentType
import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import ir.amirroid.clipshare.connectivity.signaling.SignalingService
import ir.amirroid.clipshare.connectivity.sync.SyncService
import ir.amirroid.clipshare.database.dao.clipboard.ClipboardDao
import ir.amirroid.clipshare.database.dao.device.DeviceDao
import ir.amirroid.clipshare.database.dao.sync_status.SyncStatusDao
import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.entity.ClipboardType
import ir.amirroid.clipshare.process.chunk.FileChunkManager
import ir.amirroid.clipshare.process.models.FileBufferChunked
import ir.amirroid.clipshare.storage.PlatformStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ClipboardConnectionManagerImpl(
    private val deviceDao: DeviceDao,
    private val syncService: SyncService,
    private val signalingService: SignalingService,
    private val connectionRegistry: ConnectionRegistry,
    private val storage: PlatformStorage,
    private val chunkManager: FileChunkManager,
    private val json: Json,
    private val clipboardManager: ClipboardManager,
    private val syncStatusDao: SyncStatusDao,
    private val clipboardDao: ClipboardDao,
    private val dispatcher: CoroutineDispatcher
) : ClipboardConnectionManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val receivedFileGroups = mutableMapOf<String, MutableList<String>>()
    private val groupMutex = Mutex()

    override fun start() {
        if (syncService.isStarted.not()) syncService.start()
        signalingService.onConnected {
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

            devices.filter { it.id !in currentAllConnectedDevices && !it.isHost }.forEach {
                syncService.announceOnline(it.id)
            }

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

                scope.launch(Dispatchers.Default) {
                    val successfulIds = entities.mapNotNull { entity ->
                        val requests = createRequestFromEntity(entity) ?: return@mapNotNull null
                        runCatching {
                            requests.forEach { connection.sendMessage(json.encodeToString(it)) }
                            entity.id
                        }.getOrNull()
                    }
                    if (successfulIds.isNotEmpty()) {
                        syncStatusDao.markAsSynced(deviceId, clipboardIds = successfulIds)
                    }
                }
            }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun createRequestFromEntity(entity: ClipboardEntity): List<ClipboardContentRequest>? {
        return when (entity.type) {
            ClipboardType.FILES -> {
                val paths = json.decodeFromString<List<String>>(entity.data).filter {
                    val info = storage.getFileInfo(it)
                    info.exists && !info.isDirectory
                }.takeIf { it.isNotEmpty() } ?: return null

                val group = Uuid.random().toString()

                val buffers = paths.flatMapIndexed { index, path ->
                    val chunks = chunkManager.chunkFile(path, MAX_CHUNK_SIZE_KB, group)
                    if (index == paths.lastIndex) {
                        chunks.map { it.copy(isLastItemInGroup = true) }
                    } else chunks
                }

                buffers.map { buffer ->
                    ClipboardContentRequest(
                        type = ClipboardContentType.FILES,
                        data = json.encodeToString(buffer)
                    )
                }
            }

            ClipboardType.IMAGE -> {
                val info = storage.getFileInfo(entity.data)
                if (!info.exists || info.isDirectory) return null

                val buffers = chunkManager.chunkFile(entity.data, MAX_CHUNK_SIZE_KB)
                buffers.map {
                    ClipboardContentRequest(
                        type = ClipboardContentType.IMAGE,
                        data = json.encodeToString(it),
                    )
                }
            }

            else -> {
                listOf(
                    ClipboardContentRequest(
                        type = ClipboardContentType.valueOf(entity.type.name),
                        data = entity.data,
                    )
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
                    .let { processClipboardRequest(it) } ?: return@launch

                clipboardDao.insert(
                    type = ClipboardType.valueOf(request.type.name),
                    data = request.data,
                    originDeviceId = deviceId
                )
                clipboardManager.setContent(request, withMessage = false, withSaveLastItem = true)
            }
        }
    }

    private suspend fun processClipboardRequest(
        request: ClipboardContentRequest
    ): ClipboardContentRequest? = when (request.type) {

        ClipboardContentType.IMAGE -> {
            val buffer = json.decodeFromString<FileBufferChunked>(request.data)
            chunkManager.handleIncomingChunk(buffer)?.let { path ->
                request.copy(data = path)
            }
        }

        ClipboardContentType.FILES -> {
            val buffer = json.decodeFromString<FileBufferChunked>(request.data)
            chunkManager.handleIncomingChunk(buffer)?.let { path ->
                buffer.group?.let { groupId ->
                    groupMutex.withLock {
                        val groupFiles = receivedFileGroups.getOrPut(groupId) { mutableListOf() }
                        groupFiles += path
                        Logger.withTag("asdssadsdas")
                            .d { "$groupId ${buffer.isLastItemInGroup} $groupFiles" }
                        request.takeIf { buffer.isLastItemInGroup }?.copy(
                            data = json.encodeToString(groupFiles)
                        )
                    }
                }
            }
        }

        else -> request
    }

    override fun close() {
        syncService.close()
        scope.cancel()
    }

    override fun handle(content: ClipboardContent) {
        // no-op
    }

    companion object {
        const val MAX_CHUNK_SIZE_KB = 16
    }
}