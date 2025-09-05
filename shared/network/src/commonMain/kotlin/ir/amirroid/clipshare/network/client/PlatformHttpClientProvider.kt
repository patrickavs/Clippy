package ir.amirroid.clipshare.network.client

import io.ktor.client.HttpClient

interface PlatformHttpClientProvider {
    fun provide(): HttpClient
}

expect fun createHttpClientProvider(): PlatformHttpClientProvider