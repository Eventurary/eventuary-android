package com.eventurary.auth.useCases

import com.eventurary.auth.repositories.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface HasAuthTokenAsFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}

class HasAuthTokenAsFlowUseCaseImpl(
    private val tokenRepository: TokenRepository,
) : HasAuthTokenAsFlowUseCase {
    override fun invoke(): Flow<Boolean> =
        tokenRepository.authTokensFlow
            .map { it != null }
            .distinctUntilChanged()
}
