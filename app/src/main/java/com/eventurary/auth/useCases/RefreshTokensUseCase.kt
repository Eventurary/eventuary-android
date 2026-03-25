package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.data.RefreshQueryParams
import com.eventurary.auth.repositories.TokensRepository
import com.eventurary.auth.services.AuthService
import com.eventurary.auth.services.RefreshServiceResult
import com.eventurary.core.providers.ConnectivityProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val mutex = Mutex()

interface RefreshTokensUseCase {
    suspend operator fun invoke(): RefreshTokensResult
}

class RefreshTokensUseCaseImpl(
    private val authService: AuthService,
    private val isTokensExpiredUseCase: IsTokensExpiredUseCase,
    private val connectivityProvider: ConnectivityProvider,
    private val tokensRepository: TokensRepository,
) : RefreshTokensUseCase {

    override suspend fun invoke(): RefreshTokensResult {
        mutex.withLock {
            val latest = tokensRepository.getTokens()

            return when {
                latest == null -> RefreshTokensResult.LoggedOut
                !isTokensExpiredUseCase(latest) -> RefreshTokensResult.StillValid(latest)
                !connectivityProvider.isInternetConnected() -> RefreshTokensResult.Stale(latest)
                else -> refreshTokens(latest)
            }
        }
    }

    private suspend fun refreshTokens(tokens: AuthTokens): RefreshTokensResult =
        when (val result = authService.refresh(RefreshQueryParams(tokens.refreshToken))) {
            is RefreshServiceResult.Success -> RefreshTokensResult.Refreshed(result.tokens)
            is RefreshServiceResult.Failure -> RefreshTokensResult.Stale(tokens)
            is RefreshServiceResult.LoggedOut -> RefreshTokensResult.LoggedOut
        }
}
