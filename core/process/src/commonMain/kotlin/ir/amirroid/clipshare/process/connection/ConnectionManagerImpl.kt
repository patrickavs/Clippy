package ir.amirroid.clipshare.process.connection

import ir.amirroid.clipshare.clipboard.models.ClipboardContentRequest
import ir.amirroid.clipshare.clipboard.models.ClipboardContentType
import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.signaling.SignalingServiceImpl
import ir.amirroid.clipshare.connectivity.sync.SyncService
import ir.amirroid.clipshare.database.dao.device.DeviceDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ConnectionManagerImpl(
    private val deviceDao: DeviceDao,
    private val syncService: SyncService,
    private val signalingServiceImpl: SignalingServiceImpl,
    private val connectionRegistry: ConnectionRegistry,
    dispatcher: CoroutineDispatcher
) : ConnectionManager {
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
                    connection.onMessageReceived {

                    }
                }
            }
        }
    }

    override fun close() {
        syncService.close()
        scope.cancel()
    }
}