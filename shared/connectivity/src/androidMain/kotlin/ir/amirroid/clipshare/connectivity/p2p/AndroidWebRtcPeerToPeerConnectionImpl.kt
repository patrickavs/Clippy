package ir.amirroid.clipshare.connectivity.p2p

import io.ktor.util.moveToByteArray
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.models.DataChannelBuffer
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import ir.amirroid.clipshare.connectivity.models.SignalingMessageType
import ir.amirroid.clipshare.connectivity.provider.DeviceInfoProvider
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
    private val deviceInfoProvider: DeviceInfoProvider
) : PeerToPeerConnectionService {

    private var peerConnection: PeerConnection? = null
    private var receiverDataChannel: DataChannel? = null
    private var senderDataChannel: DataChannel? = null
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus
    private var messageCallback: ((DataChannelBuffer) -> Unit)? = null
    private var iceCandidateCallback: ((SignalingIceCandidate) -> Unit)? = null

    override suspend fun createOffer(targetDeviceId: String): SignalingMessage {
        val pc = getOrCreatePeerConnection()
        ensureDataChannel(pc)
        val sdp = pc.createSdp(MediaConstraints(), true)
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
        val sdp = pc.createSdp(MediaConstraints(), false)
        val signalingSdp = SignalingMapper.fromWebRtcSdp(sdp)
        return SignalingMessage(
            type = SignalingMessageType.ANSWER,
            from = deviceUidProvider.getDeviceId(),
            to = message.from,
            sdp = signalingSdp,
            sender = deviceInfoProvider.getDeviceInfo()
        )
    }

    private suspend fun PeerConnection.awaitSetRemoteDescription(sdp: SessionDescription) =
        suspendCancellableCoroutine { cont ->
            setRemoteDescription(object : SimpleSdpObserver() {
                override fun onSetSuccess() {
                    cont.resume(Unit) { _, _, _ -> }
                }

                override fun onSetFailure(error: String?) {
                    cont.resumeWithException(RuntimeException(error))
                }
            }, sdp)
        }

    override suspend fun handleAnswer(message: SignalingMessage) {
        val pc = peerConnection ?: throw IllegalStateException("PeerConnection not initialized")
        val remoteSdp = SignalingMapper.toWebRtcSdp(
            message.sdp ?: throw IllegalArgumentException("Missing SDP")
        )
        pc.awaitSetRemoteDescription(remoteSdp)
    }

    override suspend fun handleIceCandidate(message: SignalingMessage) {
        val pc = peerConnection ?: throw IllegalStateException("PeerConnection not initialized")
        val candidate = SignalingMapper.toWebRtcIce(
            message.candidate ?: throw IllegalArgumentException("Missing candidate")
        )
        pc.addIceCandidate(candidate)
    }

    override fun onIceCandidate(callback: (SignalingIceCandidate) -> Unit) {
        iceCandidateCallback = callback
    }

    override suspend fun sendMessage(message: String) {
        val dc = senderDataChannel ?: throw IllegalStateException("DataChannel is null")
        val buffer = DataChannel.Buffer(ByteBuffer.wrap(message.toByteArray(Charsets.UTF_8)), false)

        if (dc.state() == DataChannel.State.OPEN) {
            dc.send(buffer)
        } else {
            throw IllegalStateException("DataChannel is not open, state=${dc.state()}")
        }
    }

    override suspend fun sendMessage(bytes: ByteArray) {
        val dc = senderDataChannel ?: throw IllegalStateException("DataChannel is null")
        val buffer = DataChannel.Buffer(ByteBuffer.wrap(bytes), true)

        if (dc.state() == DataChannel.State.OPEN) {
            dc.send(buffer)
        } else {
            throw IllegalStateException("DataChannel is not open, state=${dc.state()}")
        }
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

    private fun getOrCreatePeerConnection(): PeerConnection {
        if (peerConnection == null) peerConnection = buildPeerConnection()
        return peerConnection!!
    }

    private fun ensureDataChannel(pc: PeerConnection) {
        if (senderDataChannel == null) {
            val init = DataChannel.Init()
            senderDataChannel = pc.createDataChannel(deviceUidProvider.getDeviceId(), init)
            registerDataChannelObserver(false)
        }
    }


    private suspend fun PeerConnection.createSdp(
        constraints: MediaConstraints,
        isOffer: Boolean
    ): SessionDescription =
        suspendCancellableCoroutine { continuation ->
            val observer = object : SimpleSdpObserver() {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    if (sdp == null) return
                    setLocalDescription(object : SimpleSdpObserver() {
                        override fun onSetSuccess() {
                            continuation.resume(sdp) { _, _, _ -> }
                        }

                        override fun onSetFailure(error: String?) {
                            continuation.resumeWithException(RuntimeException("Failed to set local description: $error"))
                        }
                    }, sdp)
                }

                override fun onCreateFailure(error: String?) {
                    continuation.resumeWithException(RuntimeException("Failed to create SDP: $error"))
                }
            }
            if (isOffer) createOffer(observer, constraints) else createAnswer(observer, constraints)
        }

    private fun buildPeerConnection(): PeerConnection {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        return peerConnectionFactory.createPeerConnection(
            rtcConfig,
            object : PeerConnection.Observer {
                override fun onIceCandidate(candidate: IceCandidate) {
                    val msg = SignalingMapper.fromWebRtcIce(candidate)
                    iceCandidateCallback?.invoke(msg)
                }

                override fun onDataChannel(dc: DataChannel) {
                    receiverDataChannel = dc
                    registerDataChannelObserver(true)
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    updateConnectionStatus()
                }

                override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                    updateConnectionStatus()
                }

                override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {
                    if (newState == PeerConnection.IceGatheringState.COMPLETE) updateConnectionStatus()
                }

                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate?>?) {}
                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
                override fun onIceConnectionReceivingChange(p0: Boolean) {}
                override fun onAddStream(p0: MediaStream?) {}
                override fun onRemoveStream(p0: MediaStream?) {}
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
            }
        ) ?: throw IllegalStateException("Failed to build PeerConnection")
    }

    private fun registerDataChannelObserver(isReceiver: Boolean) {
        val dataChannel = if (isReceiver) {
            receiverDataChannel
        } else senderDataChannel
        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onStateChange() {
                updateConnectionStatus(isReceiver)
            }

            override fun onMessage(buffer: DataChannel.Buffer) {
                if (isReceiver.not()) return
                messageCallback?.invoke(
                    DataChannelBuffer(
                        data = buffer.data.moveToByteArray(),
                        binary = buffer.binary
                    )
                )
            }

            override fun onBufferedAmountChange(p0: Long) {}
        })
    }

    private fun ByteBuffer.decodeString(): String {
        val bytes = ByteArray(remaining())
        get(bytes)
        return String(bytes, Charsets.UTF_8)
    }

    private fun updateConnectionStatus(isReceiver: Boolean = true) {
        val dataChannel = if (isReceiver) receiverDataChannel else senderDataChannel
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