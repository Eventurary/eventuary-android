package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.services.AuthService
import com.eventurary.auth.services.RefreshResult
import com.eventurary.core.providers.ConnectivityProvider

interface RefreshTokenUseCase {
    suspend operator fun invoke(tokens: AuthTokens): AuthTokens?
}

class RefreshTokenUseCaseImpl(
    private val authService: AuthService,
    private val isTokensExpiredUseCase: IsTokensExpiredUseCase,
    private val connectivityProvider: ConnectivityProvider,
) : RefreshTokenUseCase {

    override suspend fun invoke(tokens: AuthTokens): AuthTokens? {
        return when {
            !isTokensExpiredUseCase(tokens) -> tokens
            !connectivityProvider.isInternetConnected() -> tokens
            else -> refreshTokens(tokens)
        }
    }

    private suspend fun refreshTokens(tokens: AuthTokens): AuthTokens? =
        when (val result = authService.refresh(tokens.refreshToken)) {
            is RefreshResult.Success -> result.tokens
            is RefreshResult.Failure -> tokens
            is RefreshResult.LoggedOut -> null
        }
}
