package com.eventurary.auth.repositories

import com.eventurary.auth.data.AuthTokens
import kotlinx.coroutines.flow.Flow

interface TokensRepository {
    val authTokensFlow: Flow<AuthTokens?>
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getTokens(): AuthTokens?
    suspend fun clearTokens()
}

