package com.edufelip.finn.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import platform.Foundation.NSLocale
import platform.Foundation.NSProcessInfo

internal fun createIosHttpClient(): HttpClient =
    HttpClient(Darwin) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    isLenient = true
                },
            )
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("FinnHttpClient: $message")
                }
            }
            level = LogLevel.INFO
        }
        install(DefaultRequest) {
            url {
                encodedParameters.clear()
            }
            header(HttpHeaders.Accept, "application/json")
            header(HttpHeaders.UserAgent, "Finn-iOS/${NSProcessInfo.processInfo.operatingSystemVersionString}")
            NSLocale.currentLocale.languageCode?.let { header(HttpHeaders.AcceptLanguage, it) }
        }
        install(HttpRequestRetry) {
            retryOnExceptionOrServerErrors(maxRetries = 2)
            exponentialDelay()
        }
        install(HttpResponseValidator) {
            handleResponseExceptionWithRequest { cause, _ ->
                println("FinnHttpClient error: ${cause.message}")
            }
        }
        engine {
            configureRequest {
                setValue("application/json", "Accept")
            }
        }
    }
