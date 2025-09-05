package ir.amirroid.clipshare.network.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

class DesktopHttpClientProvider : PlatformHttpClientProvider {
    override fun provide(): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                config {
                    retryOnConnectionFailure(true)
                }
            }
        }
    }
}

actual fun createHttpClientProvider(): PlatformHttpClientProvider {
    return DesktopHttpClientProvider()
}