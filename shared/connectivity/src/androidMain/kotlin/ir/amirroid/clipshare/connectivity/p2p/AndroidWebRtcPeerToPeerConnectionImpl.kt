package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import java.nio.ByteBuffer
import kotlin.coroutines.resumeWithException

class AndroidWebRtcPeerToPeerConnectionImpl(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val deviceUidProvider: DeviceUidProvider,
) : PeerToPeerConnectionService {

    private var peerConnection: PeerConnection? = null
    private var dataChannel: DataChannel? = null
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus
    private var messageCallback: ((String) -> Unit)? = null
    private var iceCandidateCallback: ((SignalingIceCandidate) -> Unit)? = null

    override suspend fun createOffer(targetDeviceId: String): SignalingMessage {
        val pc = getOrCreatePeerConnection()
        if (dataChannel == null) {
            val init = DataChannel.Init()
            dataChannel = pc.createDataChannel(PeerToPeerConnectionService.DATA_CHANNEL_LABEL, init)
            registerDataChannelObserver()
        }
        val sdp = pc.createSdp(MediaConstraints(), true)
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
        val remoteSdp = SignalingMapper.toWebRtcSdp(
            message.sdp ?: throw IllegalArgumentException("Missing SDP")
        )
        pc.setRemoteDescription(SimpleSdpObserver(), remoteSdp)
        val sdp = pc.createSdp(MediaConstraints(), false)
        val signalingSdp = SignalingMapper.fromWebRtcSdp(sdp)
        return SignalingMessage(
            type = SignalingMessageType.ANSWER,
            from = deviceUidProvider.getDeviceId(),
            to = message.from,
            sdp = signalingSdp
        )
    }

    override suspend fun handleAnswer(message: SignalingMessage) {
        val pc = peerConnection ?: throw IllegalStateException("PeerConnection not initialized")
        val remoteSdp = SignalingMapper.toWebRtcSdp(
            message.sdp ?: throw IllegalArgumentException("Missing SDP")
        )
        pc.setRemoteDescription(SimpleSdpObserver(), remoteSdp)
    }

    override suspend fun handleIceCandidate(message: SignalingMessage) {
        val pc = peerConnection ?: throw IllegalStateException("PeerConnection not initialized")
        val candidate = SignalingMapper.toWebRtcIce(
            message.candidate ?: throw IllegalArgumentException("Missing candidate")
        )
        pc.addIceCandidate(candidate)
    }

    override fun onIceCandidate(callback: (SignalingIceCandidate) -> Unit) {
        this.iceCandidateCallback = callback
    }

    override suspend fun sendMessage(message: String) {
        val buffer = DataChannel.Buffer(ByteBuffer.wrap(message.toByteArray(Charsets.UTF_8)), false)
        dataChannel?.send(buffer)
    }

    override fun onMessageReceived(action: (String) -> Unit) {
        messageCallback = action
        dataChannel?.let { registerDataChannelObserver() }
    }

    override fun close() {
        dataChannel?.close()
        dataChannel = null
        peerConnection?.close()
        peerConnection = null
    }

    private fun getOrCreatePeerConnection(): PeerConnection {
        if (peerConnection == null) peerConnection = buildPeerConnection()
        return peerConnection!!
    }

    private suspend fun PeerConnection.createSdp(
        constraints: MediaConstraints,
        isOffer: Boolean
    ): SessionDescription =
        suspendCancellableCoroutine { continuation ->
            val observer = object : SimpleSdpObserver() {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    if (sdp == null) return
                    setLocalDescription(SimpleSdpObserver(), sdp)
                    continuation.resume(sdp) {}
                }

                override fun onCreateFailure(error: String?) {
                    continuation.resumeWithException(RuntimeException("Failed to create SDP: $error"))
                }
            }
            if (isOffer) createOffer(observer, constraints) else createAnswer(observer, constraints)
        }

    private fun buildPeerConnection(): PeerConnection {
        val rtcConfig = PeerConnection.RTCConfiguration(emptyList())
        return peerConnectionFactory.createPeerConnection(
            rtcConfig,
            object : PeerConnection.Observer {
                override fun onIceCandidate(candidate: IceCandidate) {
                    val message = SignalingMapper.fromWebRtcIce(candidate)
                    iceCandidateCallback?.invoke(message)
                }

                override fun onDataChannel(dc: DataChannel) {
                    dataChannel = dc
                    registerDataChannelObserver()
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    updateConnectionStatus()
                }

                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate?>?) {}
                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
                override fun onIceConnectionReceivingChange(p0: Boolean) {}
                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
                override fun onAddStream(p0: MediaStream?) {}
                override fun onRemoveStream(p0: MediaStream?) {}
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
            }) ?: throw IllegalStateException("Failed to build PeerConnection")
    }

    private fun registerDataChannelObserver() {
        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onStateChange() {
                updateConnectionStatus()
            }

            override fun onMessage(buffer: DataChannel.Buffer) {
                val text = buffer.data.decodeString()
                messageCallback?.invoke(text)
            }

            override fun onBufferedAmountChange(p0: Long) {}
        })
    }

    private fun ByteBuffer.decodeString(): String {
        val bytes = ByteArray(remaining())
        get(bytes)
        return String(bytes, Charsets.UTF_8)
    }

    private fun updateConnectionStatus() {
        val status = when {
            peerConnection == null -> ConnectionStatus.DISCONNECTED
            peerConnection?.iceConnectionState() == PeerConnection.IceConnectionState.CONNECTED &&
                    dataChannel?.state() == DataChannel.State.OPEN -> ConnectionStatus.CONNECTED

            peerConnection?.iceConnectionState() == PeerConnection.IceConnectionState.DISCONNECTED ||
                    peerConnection?.iceConnectionState() == PeerConnection.IceConnectionState.FAILED -> ConnectionStatus.DISCONNECTED

            else -> ConnectionStatus.CONNECTING
        }
        _connectionStatus.value = status
    }
}