package ir.amirroid.clipshare.connectivity.sync

import ir.amirroid.clipshare.connectivity.models.DiscoveredDevice
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import ir.amirroid.clipshare.connectivity.p2p.PeerToPeerConnectionService
import ir.amirroid.clipshare.connectivity.signaling.SignalingService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SyncServiceImpl(
    private val signalingService: SignalingService,
    private val peerConnectionService: PeerToPeerConnectionService,
    dispatcher: CoroutineDispatcher
) : SyncService {
    private val scope = CoroutineScope(dispatcher)

    override suspend fun start() {
        signalingService.connect()
        handleSignalingEventListener()
    }

    private fun handleSignalingEventListener() {
        signalingService.onMessage { message ->
            scope.launch {
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
            }
        }
    }

    override suspend fun call(target: DiscoveredDevice) {
        val offer = peerConnectionService.createOffer(target.deviceId)
        signalingService.sendMessage(offer)
    }

    override fun close() {
        scope.cancel()
    }
}