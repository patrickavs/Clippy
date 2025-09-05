package ir.amirroid.clipshare.connectivity.models

import kotlinx.serialization.Serializable

@Serializable
data class SignalingMessage(
    val type: SignalingMessageType,
    val from: String,
    val to: String,
    val sdp: SignalingSdp? = null,
    val candidate: SignalingIceCandidate? = null
)

enum class SignalingMessageType {
    OFFER,
    ANSWER,
    ICE_CANDIDATE
}

@Serializable
data class SignalingSdp(
    val type: SdpType,
    val sdp: String
)

enum class SdpType {
    OFFER,
    ANSWER
}

@Serializable
data class SignalingIceCandidate(
    val sdpMid: String,
    val sdpMLineIndex: Int,
    val candidate: String
)