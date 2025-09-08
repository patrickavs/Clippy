package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import kotlinx.coroutines.suspendCancellableCoroutine
import dev.onvoid.webrtc.*
import dev.onvoid.webrtc.media.MediaStream
import io.ktor.util.decodeString
import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DesktopWebRtcPeerToPeerConnectionImpl(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val deviceUidProvider: DeviceUidProvider,
) : PeerToPeerConnectionService {

    private var peerConnection: RTCPeerConnection? = null
    private var dataChannel: RTCDataChannel? = null
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus
    private var messageCallback: ((String) -> Unit)? = null
    private var iceCandidateCallback: ((SignalingIceCandidate) -> Unit)? = null

    override suspend fun createOffer(targetDeviceId: String): SignalingMessage {
        val pc = getOrCreatePeerConnection()
        ensureDataChannel(pc)
        val sdp = pc.createSdp(true)
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
        pc.setRemoteDescription(remoteSdp, SimpleSdpObserver())
        ensureDataChannel(pc)
        val sdp = pc.createSdp(false)
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
        pc.setRemoteDescription(remoteSdp, SimpleSdpObserver())
    }

    override suspend fun handleIceCandidate(message: SignalingMessage) {
        val pc = peerConnection ?: throw IllegalStateException("PeerConnection not initialized")
        val candidate = SignalingMapper.toWebRtcIce(
            message.candidate ?: throw IllegalArgumentException("Missing candidate")
        )
        pc.addIceCandidate(candidate)
    }

    override suspend fun sendMessage(message: String) {
        val buffer = RTCDataChannelBuffer(ByteBuffer.wrap(message.encodeToByteArray()), false)
        if (dataChannel?.state == RTCDataChannelState.OPEN) {
            dataChannel?.send(buffer)
        }
    }

    override fun onIceCandidate(callback: (SignalingIceCandidate) -> Unit) {
        iceCandidateCallback = callback
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
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }

    private fun getOrCreatePeerConnection(): RTCPeerConnection {
        if (peerConnection == null) {
            val rtcConfig = RTCConfiguration().apply {
                val iceServer = RTCIceServer().apply { urls.add("stun:stun.l.google.com:19302") }
                iceServers.add(iceServer)
            }

            peerConnection = peerConnectionFactory.createPeerConnection(
                rtcConfig,
                object : PeerConnectionObserver {
                    override fun onIceCandidate(candidate: RTCIceCandidate) {
                        val msg = SignalingMapper.fromWebRtcIce(candidate)
                        iceCandidateCallback?.invoke(msg)
                    }

                    override fun onDataChannel(dc: RTCDataChannel) {
                        dataChannel = dc
                        registerDataChannelObserver()
                    }

                    override fun onConnectionChange(newState: RTCPeerConnectionState) {
                        updateConnectionStatus()
                    }

                    override fun onIceConnectionChange(state: RTCIceConnectionState) {
                        if (state == RTCIceConnectionState.DISCONNECTED || state == RTCIceConnectionState.FAILED) {
                            _connectionStatus.value = ConnectionStatus.DISCONNECTED
                        }
                    }

                    override fun onIceCandidatesRemoved(p0: Array<out RTCIceCandidate>?) {}
                    override fun onSignalingChange(p0: RTCSignalingState) {}
                    override fun onAddStream(p0: MediaStream) {}
                    override fun onRemoveStream(p0: MediaStream) {}
                    override fun onRenegotiationNeeded() {}
                    override fun onAddTrack(
                        receiver: RTCRtpReceiver,
                        streams: Array<out MediaStream>
                    ) {
                    }
                }) ?: throw IllegalStateException("Failed to build PeerConnection")
        }
        return peerConnection!!
    }

    private fun ensureDataChannel(pc: RTCPeerConnection) {
        if (dataChannel == null) {
            val init = RTCDataChannelInit()
            dataChannel = pc.createDataChannel(PeerToPeerConnectionService.DATA_CHANNEL_LABEL, init)
            registerDataChannelObserver()
        }
    }

    private fun registerDataChannelObserver() {
        dataChannel?.registerObserver(object : RTCDataChannelObserver {
            override fun onMessage(buffer: RTCDataChannelBuffer) {
                val text = buffer.data.decodeString()
                messageCallback?.invoke(text)
            }

            override fun onBufferedAmountChange(previousAmount: Long) {}
            override fun onStateChange() {
                updateConnectionStatus()
            }
        })
    }

    private fun updateConnectionStatus() {
        val status = when {
            peerConnection == null -> ConnectionStatus.DISCONNECTED
            peerConnection?.iceConnectionState == RTCIceConnectionState.CONNECTED &&
                    dataChannel?.state == RTCDataChannelState.OPEN -> ConnectionStatus.CONNECTED

            peerConnection?.iceConnectionState == RTCIceConnectionState.DISCONNECTED ||
                    peerConnection?.iceConnectionState == RTCIceConnectionState.FAILED -> ConnectionStatus.DISCONNECTED

            else -> ConnectionStatus.CONNECTING
        }
        _connectionStatus.value = status
    }

    private suspend fun RTCPeerConnection.createSdp(isOffer: Boolean): RTCSessionDescription =
        suspendCancellableCoroutine { cont ->
            val observer = object : CreateSessionDescriptionObserver {
                override fun onSuccess(description: RTCSessionDescription) {
                    setLocalDescription(description, SimpleSdpObserver())
                    cont.resume(description)
                }

                override fun onFailure(error: String) {
                    cont.resumeWithException(RuntimeException("SDP creation failed: $error"))
                }
            }
            if (isOffer) createOffer(RTCOfferOptions(), observer)
            else createAnswer(RTCAnswerOptions(), observer)
        }

    private fun ByteBuffer.decodeString(): String {
        val bytes = ByteArray(remaining())
        get(bytes)
        return String(bytes, Charsets.UTF_8)
    }
}