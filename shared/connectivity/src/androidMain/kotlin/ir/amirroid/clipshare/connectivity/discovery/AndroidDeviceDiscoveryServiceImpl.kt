package ir.amirroid.clipshare.connectivity.discovery

import ir.amirroid.clipshare.common.app.utils.Constants
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice
import ir.amirroid.clipshare.connectivity.models.RequestType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket

class AndroidDeviceDiscoveryServiceImpl(
    private val json: Json,
    private val deviceUidProvider: DeviceUidProvider
) : DeviceDiscoveryService {

    private val _incoming = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    override val incoming: StateFlow<List<DiscoveredDevice>> = _incoming

    private val _isStarted = MutableStateFlow(false)
    override val isStarted: StateFlow<Boolean> = _isStarted

    private var socket: DatagramSocket? = null
    private var receiveJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun startDiscovery() {
        if (socket != null) return

        socket = DatagramSocket(Constants.DEVICE_DISCOVERY_PORT).apply { broadcast = true }
        receiveJob = scope.launch { listenForDevices() }
        _isStarted.update { true }
    }

    override suspend fun stopDiscovery() {
        receiveJob?.cancelAndJoin()
        socket?.close()
        socket = null
        _isStarted.update { false }
    }

    // Listen for incoming UDP packets
    private fun listenForDevices() {
        val buffer = ByteArray(DeviceDiscoveryService.BUFFER_SIZE)
        while (true) {
            val packet = DatagramPacket(buffer, buffer.size)
            try {
                socket?.receive(packet) ?: continue
                val device = parsePacket(packet) ?: return
                updateIncoming(device)
            } catch (e: Exception) {
                if (socket?.isClosed != true) e.printStackTrace()
            }
        }
    }

    private fun parsePacket(packet: DatagramPacket): DiscoveredDevice? {
        val text = packet.data.decodeToString(0, packet.length)
        return parseDiscoveredDevice(text, packet.address.hostAddress.orEmpty())
    }

    private fun parseDiscoveredDevice(text: String, host: String): DiscoveredDevice? {
        return runCatching {
            json.decodeFromString<DiscoveredDevice>(text).copy(ip = host)
        }.getOrNull()
    }

    private fun updateIncoming(device: DiscoveredDevice) {
        if (device.deviceId == deviceUidProvider.getDeviceId()) return

        if (device.requestType == RequestType.ADD) {
            val current = _incoming.value.toMutableList()
            val index = current.indexOfFirst { it.ip == device.ip }
            if (index >= 0) current[index] = device else current.add(device)
            _incoming.update { current }
        } else {
            val new = _incoming.value.filter { it.deviceId != device.deviceId }
            _incoming.update { new }
        }
    }
}