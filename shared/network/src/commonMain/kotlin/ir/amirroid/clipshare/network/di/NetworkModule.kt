package ir.amirroid.clipshare.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import ir.amirroid.clipshare.common.app.utils.AppInfo
import ir.amirroid.clipshare.network.client.createHttpClientProvider
import ir.amirroid.clipshare.network.utils.SimpleLogger
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    singleOf<Logger>(::SimpleLogger)
    single<HttpClient> { createConfiguredClient(get(), get()) }
}

private fun HttpClientConfig<*>.configureContentNegotiation(json: Json) {
    install(ContentNegotiation) {
        json(json)
    }
}

private fun HttpClientConfig<*>.configureTimeout() {
    install(HttpTimeout) {
        requestTimeoutMillis = 25_000
        connectTimeoutMillis = 25_000
        socketTimeoutMillis = 25_000
    }
}

private fun HttpClientConfig<*>.configureWebSocket() {
    install(WebSockets) {
        pingIntervalMillis = 1000
    }
}


private fun HttpClientConfig<*>.configureDefaultRequest() {
    defaultRequest {
        contentType(ContentType.Application.Json)
        header(HttpHeaders.UserAgent, "${AppInfo.APP_NAME}/${AppInfo.VERSION}")
    }
}

private fun HttpClientConfig<*>.configureLogging(logger: Logger) {
    install(Logging) {
        this.logger = logger
    }
}

private fun createConfiguredClient(
    json: Json,
    logger: Logger
): HttpClient {
    return createHttpClientProvider().provide().config {
        configureContentNegotiation(json)
        configureTimeout()
        configureDefaultRequest()
        configureLogging(logger)
        configureWebSocket()
    }
}