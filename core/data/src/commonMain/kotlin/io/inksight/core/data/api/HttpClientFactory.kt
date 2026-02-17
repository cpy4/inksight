package io.inksight.core.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun createPlatformEngine(): HttpClientEngine

fun createHttpClient(engine: HttpClientEngine = createPlatformEngine()): HttpClient {
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(Logging) {
            level = LogLevel.HEADERS
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 120_000
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response ->
                response.status.value == 429 || response.status.value == 529
            }
            exponentialDelay()
        }
    }
}
