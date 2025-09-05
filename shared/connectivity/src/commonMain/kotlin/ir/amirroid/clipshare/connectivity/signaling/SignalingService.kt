package ir.amirroid.clipshare.connectivity.signaling

import ir.amirroid.clipshare.connectivity.models.SignalingMessage

interface SignalingService {
    suspend fun connect()
    fun close()
    suspend fun sendMessage(message: SignalingMessage)
    fun onMessage(action: (SignalingMessage) -> Unit)
}