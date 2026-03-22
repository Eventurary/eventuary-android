package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.repositories.TokenRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface IsLoggedInUseCase {
    suspend operator fun invoke(): Boolean
}

class IsLoggedInUseCaseImpl(
    private val tokenRepository: TokenRepository,
    private val isTokensExpiredUseCase: IsTokensExpiredUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) : IsLoggedInUseCase {

    private val mutex = Mutex()

    override suspend operator fun invoke(): Boolean {
        val tokens = getTokens()
        return tokens != null
    }

    suspend fun getTokens(): AuthTokens? {
        val tokens = tokenRepository.getTokens()

        return when {
            tokens == null -> tokens
            isTokensExpiredUseCase(tokens) -> refreshTokensWithLock()
            else -> tokens
        }
    }

    private suspend fun refreshTokensWithLock(): AuthTokens? {
        mutex.withLock {
            val tokens = tokenRepository.getTokens()

            return when {
                tokens == null -> tokens
                isTokensExpiredUseCase(tokens) -> refreshTokens(tokens)
                else -> tokens
            }
        }
    }

    private suspend fun refreshTokens(tokens: AuthTokens): AuthTokens? =
        refreshTokenUseCase(tokens)
}
