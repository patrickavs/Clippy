package ir.amirroid.clipshare.connectivity.sync

import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import ir.amirroid.clipshare.connectivity.models.SignalingSdp
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import ir.amirroid.clipshare.connectivity.pending.PendingConnectionManager
import ir.amirroid.clipshare.connectivity.provider.DeviceInfoProvider
import ir.amirroid.clipshare.connectivity.signaling.SignalingService
import ir.amirroid.clipshare.database.dao.device.DeviceDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SyncServiceImpl(
    private val signalingService: SignalingService,
    private val connectionRegistry: ConnectionRegistry,
    private val deviceUidProvider: DeviceUidProvider,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val pendingConnectionManager: PendingConnectionManager,
    private val deviceDao: DeviceDao,
    dispatcher: CoroutineDispatcher
) : SyncService, KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    override var isStarted = false

    override fun start() {
        scope.launch {
            signalingService.connect()
            isStarted = true
            handleSignalingEvents()
        }
    }

    private fun handleSignalingEvents() {
        signalingService.onMessage { message ->
            scope.launch {
                when (message.type) {
                    SignalingMessageType.REJECT -> {
                        connectionRegistry.removeConnection(deviceId = message.from)
                        deviceDao.removeDevice(message.from)
                    }

                    SignalingMessageType.ANNOUNCE_ONLINE -> {
                        call(message.from)
                    }

                    SignalingMessageType.OFFER -> {
                        handleIncomingOffer(message)
                    }

                    SignalingMessageType.ANSWER -> {
                        connectionRegistry.getConnection(message.from)?.handleAnswer(message)
                    }

                    SignalingMessageType.ICE_CANDIDATE -> {
                        runCatching {
                            connectionRegistry.getConnection(message.from)
                                ?.handleIceCandidate(message)
                        }.onFailure { it.printStackTrace() }
                    }
                }
            }
        }
    }

    private suspend fun handleOffer(message: SignalingMessage) {
        val connection =
            connectionRegistry.getConnection(message.from) ?: createConnection(
                message.from
            )
        val answer = connection.handleOffer(message)
        signalingService.sendMessage(answer)
    }

    private suspend fun handleIncomingOffer(message: SignalingMessage) {
        if (deviceDao.checkExistsDeviceById(message.from)) {
            handleOffer(message)
        } else {
            pendingConnectionManager.addNewPending(message.sender, message)
        }
    }

    private suspend fun sendConnectionMessage(
        to: String,
        type: SignalingMessageType,
        sdp: SignalingSdp? = null
    ) {
        signalingService.sendMessage(
            SignalingMessage(
                type = type,
                from = deviceUidProvider.getDeviceId(),
                to = to,
                sender = deviceInfoProvider.getDeviceInfo(),
                sdp = sdp
            )
        )
    }

    private fun createConnection(deviceId: String): PeerToPeerConnectionService {
        val connection: PeerToPeerConnectionService = getKoin().get()
        connection.onIceCandidate { candidate ->
            scope.launch {
                signalingService.sendMessage(
                    createSignalingMessageForIceCandidate(
                        deviceId,
                        candidate
                    )
                )
            }
        }
        connectionRegistry.addConnection(deviceId, connection)
        return connection
    }


    private fun createSignalingMessageForIceCandidate(
        targetDeviceId: String,
        candidate: SignalingIceCandidate
    ): SignalingMessage {
        return SignalingMessage(
            type = SignalingMessageType.ICE_CANDIDATE,
            from = deviceUidProvider.getDeviceId(),
            to = targetDeviceId,
            candidate = candidate,
            sender = deviceInfoProvider.getDeviceInfo()
        )
    }

    override suspend fun call(targetDeviceId: String) {
        if (connectionRegistry.hasOutgoingOfferWithTimeout(targetDeviceId)) return

        val connection =
            connectionRegistry.getConnection(targetDeviceId) ?: createConnection(targetDeviceId)
        val offer = connection.createOffer(targetDeviceId)
        signalingService.sendMessage(offer)
    }

    override suspend fun acceptConnection(targetDeviceId: String) {
        val message = pendingConnectionManager.getMessage(targetDeviceId)
        pendingConnectionManager.removePending(targetDeviceId)
        message ?: return
        handleOffer(message)
    }

    override suspend fun rejectConnection(targetDeviceId: String) {
        pendingConnectionManager.removePending(targetDeviceId)
        sendConnectionMessage(targetDeviceId, SignalingMessageType.REJECT)
    }

    override suspend fun announceOnline(targetDeviceId: String) {
        sendConnectionMessage(targetDeviceId, SignalingMessageType.ANNOUNCE_ONLINE)
    }

    override fun close() {
        connectionRegistry.allConnections().forEach { it.close() }
        signalingService.close()
        scope.cancel()
        isStarted = false
    }
}