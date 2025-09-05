package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import kotlinx.coroutines.suspendCancellableCoroutine
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import kotlin.coroutines.resumeWithException

class AndroidWebRtcPeerToPeerConnectionImpl(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val deviceUidProvider: DeviceUidProvider
) : PeerToPeerConnectionService {

    private var peerConnection: PeerConnection? = null

    override suspend fun createOffer(targetDeviceId: String): SignalingMessage {
        val pc = getOrCreatePeerConnection()

        val sdp = pc.createSdp(MediaConstraints(), isOffer = true)
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
        pc.setRemoteDescription(SimpleSdpObserver(), remoteSdp)

        val sdp = pc.createSdp(MediaConstraints(), isOffer = false)
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
        pc.setRemoteDescription(SimpleSdpObserver(), remoteSdp)
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

    private fun getOrCreatePeerConnection(): PeerConnection {
        if (peerConnection == null) {
            peerConnection = buildPeerConnection()
        }
        return peerConnection!!
    }

    private suspend fun PeerConnection.createSdp(
        constraints: MediaConstraints,
        isOffer: Boolean
    ): SessionDescription = suspendCancellableCoroutine { continuation ->
        val observer = object : SimpleSdpObserver() {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                sdp ?: return
                setLocalDescription(SimpleSdpObserver(), sdp)
                continuation.resume(sdp) {}
            }

            override fun onCreateFailure(error: String?) {
                continuation.resumeWithException(
                    RuntimeException("Failed to create SDP: $error")
                )
            }
        }

        if (isOffer) {
            createOffer(observer, constraints)
        } else {
            createAnswer(observer, constraints)
        }
    }

    private fun buildPeerConnection(): PeerConnection {
        val rtcConfig = PeerConnection.RTCConfiguration(emptyList())
        return peerConnectionFactory.createPeerConnection(
            rtcConfig, object : PeerConnection.Observer {
                override fun onIceCandidate(candidate: IceCandidate) {}

                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate?>?) {}
                override fun onDataChannel(dc: DataChannel) {}
                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {}
                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
                override fun onIceConnectionReceivingChange(p0: Boolean) {}
                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
                override fun onAddStream(p0: MediaStream?) {}
                override fun onRemoveStream(p0: MediaStream?) {}
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
            }
        ) ?: error("Failed to build PeerConnection")
    }
}