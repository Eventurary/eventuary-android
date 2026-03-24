package com.eventurary.auth.services

import com.eventurary.auth.api.AuthApi
import com.eventurary.auth.api.AuthApiResult
import com.eventurary.auth.data.LoginQueryParams
import com.eventurary.auth.data.RefreshQueryParams
import com.eventurary.auth.data.RegisterQueryParams
import com.eventurary.auth.mappers.AuthTokensMapper
import com.eventurary.auth.repositories.TokensRepository
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode

class AuthServiceImpl(
    private val authApi: AuthApi,
    private val tokensRepository: TokensRepository,
    private val authTokensMapper: AuthTokensMapper,
) : AuthService {

    companion object {
        const val MIN_CLIENT_API_FAIL_STATUS_CODE = 400
        const val MAX_CLIENT_API_FAIL_STATUS_CODE = 499
    }

    override suspend fun login(loginQueryParams: LoginQueryParams): AuthServiceResult {
        val result = authApi.login(loginQueryParams)
        return handleLogin(result)
    }

    override suspend fun register(registerQueryParams: RegisterQueryParams): AuthServiceResult {
        val result = authApi.register(registerQueryParams)
        return handleLogin(result)
    }

    override suspend fun refresh(refreshQueryParams: RefreshQueryParams): RefreshServiceResult {
        val result = authApi.refresh(refreshQueryParams)
        return handleRefresh(authApiResult = result)
    }

    override suspend fun logout() {
        tokensRepository.clearTokens()
    }

    private suspend fun handleLogin(authApiResult: AuthApiResult): AuthServiceResult {
        return when (authApiResult) {
            is AuthApiResult.Success -> {
                val tokensDTO = authApiResult.response
                val tokens = authTokensMapper.map(tokensDTO)
                tokensRepository.saveTokens(tokens)

                AuthServiceResult.Success(tokens)
            }

            else -> {
                // TODO: Make more descriptive
                AuthServiceResult.Error("Error on login - to be described")
            }
        }
    }

    private suspend fun handleRefresh(authApiResult: AuthApiResult): RefreshServiceResult {
        return when (authApiResult) {
            is AuthApiResult.Success -> {
                Napier.d { "Refresh successful" }
                authTokensMapper.map(authApiResult.response)
                    .also { tokensRepository.saveTokens(it) }
                    .let { RefreshServiceResult.Success(it) }
            }

            is AuthApiResult.APIError -> handleRefreshApiError(authApiResult)

            else -> {
                Napier.d { "Refresh failed" }
                RefreshServiceResult.Failure
            }
        }
    }

    private suspend fun handleRefreshApiError(error: AuthApiResult.APIError): RefreshServiceResult {
        return if (error.status.shouldLogout) {
            Napier.d { "Refresh api failure causing logout" }
            tokensRepository.clearTokens()
            RefreshServiceResult.LoggedOut
        } else {
            Napier.d { "Refresh api failure not causing logout" }
            RefreshServiceResult.Failure
        }
    }

    private val HttpStatusCode.shouldLogout: Boolean
        get() = value in MIN_CLIENT_API_FAIL_STATUS_CODE..MAX_CLIENT_API_FAIL_STATUS_CODE
}
