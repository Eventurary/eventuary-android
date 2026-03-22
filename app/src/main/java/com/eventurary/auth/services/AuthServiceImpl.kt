package com.eventurary.auth.services

import com.eventurary.auth.api.AuthApi
import com.eventurary.auth.api.AuthApiResult
import com.eventurary.auth.data.LoginRequest
import com.eventurary.auth.data.RegisterRequest
import com.eventurary.auth.mappers.AuthTokensMapper
import com.eventurary.auth.repositories.TokenRepository
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode

class AuthServiceImpl(
    private val authApi: AuthApi,
    private val tokenRepository: TokenRepository,
    private val authTokensMapper: AuthTokensMapper,
) : AuthService {

    companion object {
        const val MIN_CLIENT_API_FAIL_STATUS_CODE = 400
        const val MAX_CLIENT_API_FAIL_STATUS_CODE = 499
    }

    override suspend fun login(loginRequest: LoginRequest): AuthResult {
        val result = authApi.login(loginRequest)
        return handleLogin(result)
    }

    override suspend fun register(registerRequest: RegisterRequest): AuthResult {
        val result = authApi.register(registerRequest)
        return handleLogin(result)
    }

    override suspend fun refresh(refreshToken: String): RefreshResult {
        val result = authApi.refresh(refreshToken)
        return handleRefresh(authApiResult = result)
    }

    override suspend fun logout() {
        tokenRepository.clearTokens()
    }

    private suspend fun handleLogin(authApiResult: AuthApiResult): AuthResult {
        return when (authApiResult) {
            is AuthApiResult.Success -> {
                val tokensDTO = authApiResult.response
                val tokens = authTokensMapper.map(tokensDTO)
                tokenRepository.saveTokens(tokens)

                AuthResult.Success(tokens)
            }

            else -> {
                // TODO: Make more descriptive
                AuthResult.Error("Error on login - to be described")
            }
        }
    }

    private suspend fun handleRefresh(authApiResult: AuthApiResult): RefreshResult {
        return when (authApiResult) {
            is AuthApiResult.Success -> {
                Napier.d { "Refresh successful" }
                authTokensMapper.map(authApiResult.response)
                    .also { tokenRepository.saveTokens(it) }
                    .let { RefreshResult.Success(it) }
            }

            is AuthApiResult.APIError -> handleRefreshApiError(authApiResult)

            else -> {
                Napier.d { "Refresh failed" }
                RefreshResult.Failure
            }
        }
    }

    private suspend fun handleRefreshApiError(error: AuthApiResult.APIError): RefreshResult {
        return if (error.status.shouldLogout) {
            Napier.d { "Refresh api failure causing logout" }
            tokenRepository.clearTokens()
            RefreshResult.LoggedOut
        } else {
            Napier.d { "Refresh api failure not causing logout" }
            RefreshResult.Failure
        }
    }

    private val HttpStatusCode.shouldLogout: Boolean
        get() = value in MIN_CLIENT_API_FAIL_STATUS_CODE..MAX_CLIENT_API_FAIL_STATUS_CODE
}
