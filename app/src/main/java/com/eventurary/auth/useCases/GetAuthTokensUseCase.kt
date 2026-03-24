package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.repositories.TokensRepository
import com.eventurary.auth.useCases.RefreshTokensResult.Companion.getTokens

interface GetAuthTokensUseCase {
    suspend operator fun invoke(): AuthTokens?
}

class GetAuthTokensUseCaseImpl(
    private val tokensRepository: TokensRepository,
    private val isTokensExpiredUseCase: IsTokensExpiredUseCase,
    private val refreshTokensUseCase: RefreshTokensUseCase,
) : GetAuthTokensUseCase {

    override suspend operator fun invoke(): AuthTokens? {
        val tokens = tokensRepository.getTokens()

        return when {
            tokens == null -> tokens
            isTokensExpiredUseCase(tokens) -> refreshTokens()
            else -> tokens
        }
    }

    private suspend fun refreshTokens(): AuthTokens? =
        refreshTokensUseCase().getTokens()
}
