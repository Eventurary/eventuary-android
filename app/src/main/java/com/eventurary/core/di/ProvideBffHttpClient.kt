package com.eventurary.core.di

import com.eventurary.BuildConfig
import com.eventurary.auth.useCases.GetAuthTokensUseCase
import com.eventurary.auth.useCases.RefreshTokensResult.Companion.getFreshTokens
import com.eventurary.auth.useCases.RefreshTokensUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

fun provideBffHttpClient(
    getAuthTokensUseCase: GetAuthTokensUseCase,
    refreshTokensUseCase: RefreshTokensUseCase,
    httpTimeout: Long = 10_000L,
): HttpClient = HttpClient(Android) {
    install(ContentNegotiation) { json() }
    install(Logging) { level = LogLevel.BODY }
    install(HttpTimeout) { requestTimeoutMillis = httpTimeout }
    install(DefaultRequest) { url(BuildConfig.BASE_URL) }

    install(Auth) {
        bearer {
            loadTokens {
                getAuthTokensUseCase()?.let {
                    BearerTokens(
                        it.accessToken,
                        it.refreshToken
                    )
                }
            }

            refreshTokens {
                refreshTokensUseCase().getFreshTokens()?.let {
                    BearerTokens(
                        it.accessToken,
                        it.refreshToken
                    )
                }
            }
        }
    }
}


