package ir.amirroid.clipshare.connectivity.broadcast

import ir.amirroid.clipshare.common.app.utils.Constants
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice
import ir.amirroid.clipshare.connectivity.models.DiscoveredPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class DesktopDeviceBroadcastServiceImpl(
    private val json: Json,
    private val deviceUidProvider: DeviceUidProvider
) : DeviceBroadcastService {
    private var socket: DatagramSocket? = null
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isStarted = MutableStateFlow(false)
    override val isStarted: StateFlow<Boolean> = _isStarted

    override suspend fun startBroadcasting() {
        stopBroadcasting() // ensure no duplicate job
        job = scope.launch { broadcastLoop() }
    }

    private suspend fun CoroutineScope.broadcastLoop() {
        DatagramSocket().use { s ->
            s.broadcast = true
            socket = s

            val packet = createBroadcastPacket()

            _isStarted.update { true }
            while (isActive) {
                s.send(packet)
                delay(DeviceBroadcastService.BROADCAST_INTERVAL_MS)
            }
        }
    }

    private fun createBroadcastPacket(): DatagramPacket {
        val device = buildDeviceInfo()
        val data = json.encodeToString(device).toByteArray()
        return DatagramPacket(
            data,
            data.size,
            InetAddress.getByName(Constants.BROADCAST_ADDRESS),
            Constants.DEVICE_DISCOVERY_PORT
        )
    }

    private fun buildDeviceInfo(): DiscoveredDevice {
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        val userName = System.getProperty("user.name")

        val deviceName = "$userName@$osName $osVersion"

        return DiscoveredDevice(
            name = deviceName,
            platform = DiscoveredPlatform.DESKTOP,
            deviceId = deviceUidProvider.getDeviceId()
        )
    }

    override suspend fun stopBroadcasting() {
        job?.cancelAndJoin()
        job = null
        socket?.close()
        socket = null
        _isStarted.update { false }
    }
}