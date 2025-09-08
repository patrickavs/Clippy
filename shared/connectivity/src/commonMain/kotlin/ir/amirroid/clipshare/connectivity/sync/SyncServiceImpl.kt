package ir.amirroid.clipshare.connectivity.sync

import co.touchlab.kermit.Logger
import ir.amirroid.clipshare.common.app.utils.Platform
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import ir.amirroid.clipshare.connectivity.signaling.SignalingService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class SyncServiceImpl(
    private val signalingService: SignalingService,
    private val peerConnectionService: PeerToPeerConnectionService,
    private val deviceUidProvider: DeviceUidProvider,
    dispatcher: CoroutineDispatcher
) : SyncService {


    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var targetDeviceId: String? = null

    override val connectionStatus = peerConnectionService.connectionStatus
        .distinctUntilChanged(areEquivalent = { first, second -> first == second })

    override suspend fun start() {
        signalingService.connect()
        handleSignalingEvents()
        observePeerConnection()
    }

    private fun handleSignalingEvents() {
        signalingService.onMessage { message ->
            scope.launch {
                try {
                    when (message.type) {
                        SignalingMessageType.OFFER -> {
                            val answer = peerConnectionService.handleOffer(message)
                            signalingService.sendMessage(answer)
                        }

                        SignalingMessageType.ANSWER -> {
                            peerConnectionService.handleAnswer(message)
                        }

                        SignalingMessageType.ICE_CANDIDATE -> {
                            peerConnectionService.handleIceCandidate(message)
                        }
                    }
                } catch (e: Exception) {
                    Logger.e(e) { "Error handling signaling message" }
                }
            }
        }
    }

    private fun observePeerConnection() {
        peerConnectionService.onMessageReceived { msg ->
            Logger.withTag("SYNC_SERVICE").d { "Peer message: $msg ${Platform.current()}" }
        }

        peerConnectionService.onIceCandidate { candidate ->
            scope.launch {
                targetDeviceId ?: return@launch
                signalingService.sendMessage(
                    SignalingMessage(
                        type = SignalingMessageType.ICE_CANDIDATE,
                        from = deviceUidProvider.getDeviceId(),
                        to = targetDeviceId!!,
                        candidate = candidate
                    )
                )
            }
        }
    }

    override suspend fun call(targetDeviceId: String) {
        val offer = peerConnectionService.createOffer(targetDeviceId)
        this.targetDeviceId = targetDeviceId
        signalingService.sendMessage(offer)
    }

    override fun close() {
        scope.cancel()
        signalingService.close()
        peerConnectionService.close()
    }
}