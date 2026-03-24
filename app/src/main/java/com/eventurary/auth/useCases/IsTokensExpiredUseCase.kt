package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.core.providers.DateTimeProvider
import kotlin.time.Duration.Companion.minutes

interface IsTokensExpiredUseCase {
    operator fun invoke(tokens: AuthTokens): Boolean
}

class IsTokensExpiredUseCaseImpl(
    private val dateTimeProvider: DateTimeProvider,
) : IsTokensExpiredUseCase {

    companion object {
        val expiryBufferMillis = 10.minutes.inWholeMilliseconds
    }

    override fun invoke(tokens: AuthTokens): Boolean {
        val currentTime = dateTimeProvider.nowMillis
        val expireTime = tokens.creationTime + tokens.lifeSpan

        return currentTime > (expireTime - expiryBufferMillis)
    }
}
