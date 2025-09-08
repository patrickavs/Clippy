package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.models.SdpType
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import ir.amirroid.clipshare.connectivity.models.SignalingSdp
import dev.onvoid.webrtc.*

object SignalingMapper {
    fun fromWebRtcSdp(sdp: RTCSessionDescription): SignalingSdp {
        val type = when (sdp.sdpType) {
            RTCSdpType.OFFER -> SdpType.OFFER
            RTCSdpType.ANSWER -> SdpType.ANSWER
            else -> error("Unsupported SDP type: ${sdp.sdpType}")
        }
        return SignalingSdp(type, sdp.sdp)
    }

    fun toWebRtcSdp(sdp: SignalingSdp): RTCSessionDescription {
        val type = when (sdp.type) {
            SdpType.OFFER -> RTCSdpType.OFFER
            SdpType.ANSWER -> RTCSdpType.ANSWER
        }
        return RTCSessionDescription(type, sdp.sdp)
    }

    fun fromWebRtcIce(candidate: RTCIceCandidate): SignalingIceCandidate {
        return SignalingIceCandidate(
            sdpMid = candidate.sdpMid ?: "",
            sdpMLineIndex = candidate.sdpMLineIndex,
            candidate = candidate.sdp
        )
    }

    fun toWebRtcIce(candidate: SignalingIceCandidate): RTCIceCandidate {
        return RTCIceCandidate(
            candidate.sdpMid,
            candidate.sdpMLineIndex,
            candidate.candidate
        )
    }
}