package ir.amirroid.clipshare.connectivity.sync

import ir.amirroid.clipshare.connectivity.connection.ConnectionRegistry
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import ir.amirroid.clipshare.connectivity.signaling.SignalingService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SyncServiceImpl(
    private val signalingService: SignalingService,
    private val connectionRegistry: ConnectionRegistry,
    private val deviceUidProvider: DeviceUidProvider,
    dispatcher: CoroutineDispatcher
) : SyncService, KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override suspend fun start() {
        signalingService.connect()
        handleSignalingEvents()
    }

    private fun handleSignalingEvents() {
        signalingService.onMessage { message ->
            scope.launch {
                when (message.type) {
                    SignalingMessageType.OFFER -> {
                        val conn =
                            connectionRegistry.getConnection(message.from) ?: createConnection(
                                message.from
                            )
                        val answer = conn.handleOffer(message)
                        signalingService.sendMessage(answer)
                    }

                    SignalingMessageType.ANSWER -> {
                        connectionRegistry.getConnection(message.from)?.handleAnswer(message)
                    }

                    SignalingMessageType.ICE_CANDIDATE -> {
                        connectionRegistry.getConnection(message.from)?.handleIceCandidate(message)
                    }
                }
            }
        }
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
            candidate = candidate
        )
    }

    override suspend fun call(targetDeviceId: String) {
        val connection =
            connectionRegistry.getConnection(targetDeviceId) ?: createConnection(targetDeviceId)
        val offer = connection.createOffer(targetDeviceId)
        signalingService.sendMessage(offer)
    }

    override fun close() {
        connectionRegistry.allConnections().forEach { it.close() }
        signalingService.close()
    }
}