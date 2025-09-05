package ir.amirroid.clipshare.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import ir.amirroid.clipshare.common.app.utils.AppInfo
import ir.amirroid.clipshare.network.client.createHttpClientProvider
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { createConfiguredClient(get()) }
}

private fun HttpClientConfig<*>.configureContentNegotiation(json: Json) {
    install(ContentNegotiation) {
        json(json)
    }
}

private fun HttpClientConfig<*>.configureTimeout() {
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 15_000
    }
}

private fun HttpClientConfig<*>.configureWebSocket() {
    install(WebSockets)
}


private fun HttpClientConfig<*>.configureDefaultRequest() {
    defaultRequest {
        contentType(ContentType.Application.Json)
        header(HttpHeaders.UserAgent, "${AppInfo.APP_NAME}/${AppInfo.VERSION}")
    }
}

private fun createConfiguredClient(
    json: Json
): HttpClient {
    return createHttpClientProvider().provide().config {
        configureContentNegotiation(json)
        configureTimeout()
        configureDefaultRequest()
        configureWebSocket()
    }
}