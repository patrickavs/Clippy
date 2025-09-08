package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.models.ConnectionStatus
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import kotlinx.coroutines.flow.StateFlow

interface PeerToPeerConnectionService {
    val connectionStatus: StateFlow<ConnectionStatus>

    suspend fun createOffer(targetDeviceId: String): SignalingMessage
    suspend fun handleOffer(message: SignalingMessage): SignalingMessage
    suspend fun handleAnswer(message: SignalingMessage)
    suspend fun handleIceCandidate(message: SignalingMessage)
    suspend fun sendMessage(message: String)
    fun onIceCandidate(callback: (SignalingIceCandidate) -> Unit)
    fun onMessageReceived(action: (String) -> Unit)
    fun close()

    companion object {
        const val DATA_CHANNEL_LABEL = "dataChannel"
    }
}