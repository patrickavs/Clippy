package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import kotlinx.coroutines.suspendCancellableCoroutine
import dev.onvoid.webrtc.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DesktopWebRtcPeerToPeerConnectionImpl(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val deviceUidProvider: DeviceUidProvider
) : PeerToPeerConnectionService {

    private var peerConnection: RTCPeerConnection? = null

    override suspend fun createOffer(targetDeviceId: String): SignalingMessage {
        val pc = getOrCreatePeerConnection()

        val sdp = pc.createSdp(isOffer = true)
        val signalingSdp = SignalingMapper.fromWebRtcSdp(sdp)

        return SignalingMessage(
            type = SignalingMessageType.OFFER,
            from = deviceUidProvider.getDeviceId(),
            to = targetDeviceId,
            sdp = signalingSdp
        )
    }

    override suspend fun handleOffer(message: SignalingMessage): SignalingMessage {
        val pc = getOrCreatePeerConnection()
        val remoteSdp = SignalingMapper.toWebRtcSdp(message.sdp ?: error("Missing SDP"))
        pc.setRemoteDescription(remoteSdp, null)

        val sdp = pc.createSdp(isOffer = false)
        val signalingSdp = SignalingMapper.fromWebRtcSdp(sdp)

        return SignalingMessage(
            type = SignalingMessageType.ANSWER,
            from = deviceUidProvider.getDeviceId(),
            to = message.from,
            sdp = signalingSdp
        )
    }

    override suspend fun handleAnswer(message: SignalingMessage) {
        val pc = peerConnection ?: error("PeerConnection not initialized")
        val remoteSdp = SignalingMapper.toWebRtcSdp(message.sdp ?: error("Missing SDP"))
        pc.setRemoteDescription(remoteSdp, null)
    }

    override suspend fun handleIceCandidate(message: SignalingMessage) {
        val pc = peerConnection ?: error("PeerConnection not initialized")
        val candidate = SignalingMapper.toWebRtcIce(message.candidate ?: error("Missing candidate"))
        pc.addIceCandidate(candidate)
    }

    override fun close() {
        peerConnection?.close()
        peerConnection = null
    }

    private fun getOrCreatePeerConnection(): RTCPeerConnection {
        if (peerConnection == null) {
            val rtcConfig = RTCConfiguration().apply {
                val iceServer = RTCIceServer()
                iceServer.urls.add("stun:stun.l.google.com:19302")
                iceServers.add(iceServer)
            }

            peerConnection = peerConnectionFactory.createPeerConnection(
                rtcConfig
            ) {
                // no-op
            } ?: error("Failed to build PeerConnection")
        }
        return peerConnection!!
    }

    private suspend fun RTCPeerConnection.createSdp(isOffer: Boolean): RTCSessionDescription =
        suspendCancellableCoroutine { cont ->
            val observer = object : CreateSessionDescriptionObserver {
                override fun onSuccess(description: RTCSessionDescription) {
                    setLocalDescription(description, null)
                    cont.resume(description)
                }

                override fun onFailure(error: String) {
                    cont.resumeWithException(RuntimeException("SDP creation failed: $error"))
                }
            }

            if (isOffer) {
                createOffer(RTCOfferOptions(), observer)
            } else {
                createAnswer(RTCAnswerOptions(), observer)
            }
        }
}
