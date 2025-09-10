package ir.amirroid.clipshare.process.connection

import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
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
    private val connectionRegistry: ConnectionRegistry,
    dispatcher: CoroutineDispatcher
) : ConnectionManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun start() {
        scope.launch {
            handleDevicesFromDatabase()
        }
    }

    private suspend fun handleDevicesFromDatabase() {
        deviceDao.getAllDiscoveredDevices().collect { devices ->
            val currentAllConnectedDevices = connectionRegistry.allConnectionDevices()
            val devicesIds = devices.map { it.id }

            if (devices.isNotEmpty() && syncService.isStarted.not()) syncService.start()

            devices.filter { it.id !in currentAllConnectedDevices }.forEach {
                syncService.call(it.id)
            }
            currentAllConnectedDevices.filter { it !in devicesIds }.forEach {
                connectionRegistry.removeConnection(it)
            }
        }
    }

    override fun close() {
        syncService.close()
        scope.cancel()
    }
}