package ir.amirroid.clipshare.connectivity.p2p

import dev.onvoid.webrtc.SetSessionDescriptionObserver

open class SimpleSdpObserver : SetSessionDescriptionObserver {
    override fun onSuccess() {
        // no-op
    }

    override fun onFailure(error: String?) {
        println("SimpleSdpObserver: $error")
    }
}