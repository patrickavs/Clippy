package ir.amirroid.clipshare.connectivity.p2p

import ir.amirroid.clipshare.connectivity.models.SdpType
import ir.amirroid.clipshare.connectivity.models.SignalingIceCandidate
import ir.amirroid.clipshare.connectivity.models.SignalingSdp
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

object SignalingMapper {
    fun fromWebRtcSdp(sdp: SessionDescription): SignalingSdp {
        val type = when (sdp.type) {
            SessionDescription.Type.OFFER -> SdpType.OFFER
            SessionDescription.Type.ANSWER -> SdpType.ANSWER
            else -> error("Unsupported SDP type: ${sdp.type}")
        }
        return SignalingSdp(type, sdp.description)
    }

    fun toWebRtcSdp(sdp: SignalingSdp): SessionDescription {
        val type = when (sdp.type) {
            SdpType.OFFER -> SessionDescription.Type.OFFER
            SdpType.ANSWER -> SessionDescription.Type.ANSWER
        }
        return SessionDescription(type, sdp.sdp)
    }

    fun fromWebRtcIce(candidate: IceCandidate): SignalingIceCandidate {
        return SignalingIceCandidate(
            sdpMid = candidate.sdpMid ?: "",
            sdpMLineIndex = candidate.sdpMLineIndex,
            candidate = candidate.sdp,
        )
    }

    fun toWebRtcIce(candidate: SignalingIceCandidate): IceCandidate {
        return IceCandidate(
            candidate.sdpMid,
            candidate.sdpMLineIndex,
            candidate.candidate
        )
    }
}