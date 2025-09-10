package ir.amirroid.clipshare.connectivity.signaling

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.URLProtocol
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import ir.amirroid.clipshare.connectivity.device.DeviceUidProvider
import ir.amirroid.clipshare.connectivity.models.SignalingMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SignalingServiceImpl(
    private val httpClient: HttpClient,
    private val json: Json,
    private val deviceUidProvider: DeviceUidProvider,
    dispatcher: CoroutineDispatcher
) : SignalingService {
    private var session: WebSocketSession? = null
    private var action: ((SignalingMessage) -> Unit)? = null
    private val scope = CoroutineScope(dispatcher)

    override fun connect() {
        scope.launch {
            session = httpClient.webSocketSession {
                header("uid", deviceUidProvider.getDeviceId())
                url {
                    protocol = URLProtocol.WS
                    url("ws://192.168.227.150:8080/signaling")
                }
            }
            handleFrames()
        }
    }

    private suspend fun handleFrames() {
        session?.incoming?.consumeEach { frame ->
            if (frame !is Frame.Text) return
            val text = frame.readText()
            val message = json.decodeFromString<SignalingMessage>(text)
            action?.invoke(message)
        }
    }


    override fun close() {
        action = null
        session?.cancel()
        scope.cancel()
    }

    override suspend fun sendMessage(message: SignalingMessage) {
        session?.send(Frame.Text(json.encodeToString(message)))
    }

    override fun onMessage(action: (SignalingMessage) -> Unit) {
        this.action = action
    }
}