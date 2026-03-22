package com.eventurary.auth.mappers

import com.eventurary.auth.data.AuthTokensDTO
import com.eventurary.auth.data.AuthTokens
import com.eventurary.core.providers.DateTimeProvider

class AuthTokensMapper(private val dateTimeProvider: DateTimeProvider) {

    fun map(from: AuthTokensDTO): AuthTokens {
        return AuthTokens(
            accessToken = from.accessToken,
            refreshToken = from.refreshToken,
            creationTime = dateTimeProvider.nowMillis,
            lifeSpan = from.lifeSpan,
        )
    }
}
