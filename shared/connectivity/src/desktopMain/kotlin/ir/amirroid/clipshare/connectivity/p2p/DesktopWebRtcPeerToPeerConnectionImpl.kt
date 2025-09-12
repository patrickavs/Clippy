package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import kotlinx.coroutines.suspendCancellableCoroutine
import dev.onvoid.webrtc.*
import dev.onvoid.webrtc.media.MediaStream
import io.ktor.util.moveToByteArray
import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.models.DataChannelBuffer
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import ir.amirroid.clipshare.connectivity.provider.DesktopDeviceInfoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DesktopWebRtcPeerToPeerConnectionImpl(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val deviceUidProvider: DeviceUidProvider,
    private val deviceInfoProvider: DesktopDeviceInfoProvider
) : PeerToPeerConnectionService {

    private var peerConnection: RTCPeerConnection? = null
    private var senderDataChannel: RTCDataChannel? = null
    private var receiverDataChannel: RTCDataChannel? = null
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus
    private var messageCallback: ((DataChannelBuffer) -> Unit)? = null
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
            sdp = signalingSdp,
            sender = deviceInfoProvider.getDeviceInfo()
        )
    }

    override suspend fun handleOffer(message: SignalingMessage): SignalingMessage {
        val pc = getOrCreatePeerConnection()
        val remoteSdp = SignalingMapper.toWebRtcSdp(
            message.sdp ?: throw IllegalArgumentException("Missing SDP")
        )
        pc.awaitSetRemoteDescription(remoteSdp)
        ensureDataChannel(pc)
        val sdp = pc.createSdp(false)
        val signalingSdp = SignalingMapper.fromWebRtcSdp(sdp)
        return SignalingMessage(
            type = SignalingMessageType.ANSWER,
            from = deviceUidProvider.getDeviceId(),
            to = message.from,
            sdp = signalingSdp,
            sender = deviceInfoProvider.getDeviceInfo()
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
        val dc = senderDataChannel ?: throw IllegalStateException("RTCDataChannel is null")
        if (dc.state != RTCDataChannelState.OPEN) {
            throw IllegalStateException("RTCDataChannel is not open, state=${dc.state}")
        }

        val buffer = RTCDataChannelBuffer(ByteBuffer.wrap(message.encodeToByteArray()), false)
        dc.send(buffer)
    }

    override suspend fun sendMessage(bytes: ByteArray) {
        val dc = senderDataChannel ?: throw IllegalStateException("RTCDataChannel is null")
        if (dc.state != RTCDataChannelState.OPEN) {
            throw IllegalStateException("RTCDataChannel is not open, state=${dc.state}")
        }

        val buffer = RTCDataChannelBuffer(ByteBuffer.wrap(bytes), true)
        dc.send(buffer)
    }

    override fun onIceCandidate(callback: (SignalingIceCandidate) -> Unit) {
        iceCandidateCallback = callback
    }

    override fun onMessageReceived(action: (DataChannelBuffer) -> Unit) {
        messageCallback = action
        receiverDataChannel?.let { registerDataChannelObserver(true) }
    }

    override fun close() {
        senderDataChannel?.close()
        receiverDataChannel?.close()
        senderDataChannel = null
        receiverDataChannel = null
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
                        receiverDataChannel = dc
                        registerDataChannelObserver(true)
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
        if (senderDataChannel == null) {
            val init = RTCDataChannelInit()
            senderDataChannel = pc.createDataChannel(deviceUidProvider.getDeviceId(), init)
            registerDataChannelObserver(false)
        }
    }

    private fun registerDataChannelObserver(isReceiver: Boolean) {
        val dataChannel = if (isReceiver) receiverDataChannel else senderDataChannel
        dataChannel?.registerObserver(object : RTCDataChannelObserver {
            override fun onMessage(buffer: RTCDataChannelBuffer) {
                if (isReceiver.not()) return
                messageCallback?.invoke(
                    DataChannelBuffer(
                        data = buffer.data.moveToByteArray(),
                        binary = buffer.binary
                    )
                )
            }

            override fun onBufferedAmountChange(previousAmount: Long) {}
            override fun onStateChange() {
                updateConnectionStatus(isReceiver)
            }
        })
    }

    private fun updateConnectionStatus(isReceiver: Boolean = true) {
        val dataChannel = if (isReceiver) receiverDataChannel else senderDataChannel
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
                    setLocalDescription(description, object : SetSessionDescriptionObserver {
                        override fun onSuccess() {
                            cont.resume(description)
                        }

                        override fun onFailure(error: String?) {
                            cont.resumeWithException(RuntimeException("Failed to set local description: $error"))
                        }

                    })
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

    private suspend fun RTCPeerConnection.awaitSetLocalDescription(sdp: RTCSessionDescription) =
        suspendCancellableCoroutine { cont ->
            setLocalDescription(sdp, object : SetSessionDescriptionObserver {
                override fun onSuccess() {
                    cont.resume(Unit) {}
                }

                override fun onFailure(error: String?) {
                    cont.resumeWithException(RuntimeException("Failed to set local description: $error"))
                }
            })
        }

    private suspend fun RTCPeerConnection.awaitSetRemoteDescription(sdp: RTCSessionDescription) =
        suspendCancellableCoroutine { cont ->
            setRemoteDescription(sdp, object : SetSessionDescriptionObserver {
                override fun onSuccess() {
                    cont.resume(Unit) {}
                }

                override fun onFailure(error: String?) {
                    cont.resumeWithException(RuntimeException("Failed to set remote description: $error"))
                }
            })
        }
}