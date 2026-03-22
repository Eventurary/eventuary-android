package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.core.providers.DateTimeProvider

interface IsTokensExpiredUseCase {
    operator fun invoke(tokens: AuthTokens): Boolean
}

class IsTokensExpiredUseCaseImpl(
    private val dateTimeProvider: DateTimeProvider
) : IsTokensExpiredUseCase {

    override fun invoke(tokens: AuthTokens): Boolean {
        val currentTime = dateTimeProvider.nowMillis
        val expireTime = tokens.creationTime + tokens.lifeSpan
        return currentTime > expireTime
    }
}
