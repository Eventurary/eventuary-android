package com.eventurary.auth.useCases

import com.eventurary.auth.repositories.TokensRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface HasAuthTokenAsFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}

class HasAuthTokenAsFlowUseCaseImpl(
    private val tokensRepository: TokensRepository,
) : HasAuthTokenAsFlowUseCase {
    override fun invoke(): Flow<Boolean> =
        tokensRepository.authTokensFlow
            .map { it != null }
            .distinctUntilChanged()
}
