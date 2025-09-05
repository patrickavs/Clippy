package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.models.SignalingMessage

interface PeerToPeerConnectionService {
    suspend fun createOffer(targetDeviceId: String): SignalingMessage
    suspend fun handleOffer(message: SignalingMessage): SignalingMessage
    suspend fun handleAnswer(message: SignalingMessage)
    suspend fun handleIceCandidate(message: SignalingMessage)
    fun close()
}