package ir.amirroid.clipshare.connectivity.signaling

import ir.amirroid.clipshare.connectivity.models.SignalingMessage

interface SignalingService {
    fun connect()
    fun close()
    suspend fun sendMessage(message: SignalingMessage)
    fun onConnected(action: () -> Unit)
    fun onMessage(action: (SignalingMessage) -> Unit)
}