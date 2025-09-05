package ir.amirroid.clipshare.connectivity.p2p

import co.touchlab.kermit.Logger
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class SimpleSdpObserver : SdpObserver {
    override fun onCreateSuccess(sdp: SessionDescription?) {
        Logger.d("onCreateSuccess: ${sdp?.description}")
    }

    override fun onSetSuccess() {
        Logger.d("onSetSuccess")
    }

    override fun onCreateFailure(error: String?) {
        Logger.d("onCreateFailure: $error")
    }

    override fun onSetFailure(error: String?) {
        Logger.d("onSetFailure: $error")
    }
}