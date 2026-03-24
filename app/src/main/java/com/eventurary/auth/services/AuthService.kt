package com.eventurary.auth.services

import com.eventurary.auth.data.LoginQueryParams
import com.eventurary.auth.data.RegisterQueryParams
import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.data.RefreshQueryParams

sealed class AuthServiceResult {
    data class Success(val tokens: AuthTokens) : AuthServiceResult()
    data class Error(val message: String) : AuthServiceResult()
}

sealed class RefreshServiceResult {
    data class Success(val tokens: AuthTokens) : RefreshServiceResult()
    object Failure : RefreshServiceResult()
    object LoggedOut : RefreshServiceResult()
}

interface AuthService {
    suspend fun login(loginQueryParams: LoginQueryParams): AuthServiceResult
    suspend fun register(registerQueryParams: RegisterQueryParams): AuthServiceResult
    suspend fun refresh(refreshQueryParams: RefreshQueryParams): RefreshServiceResult
    suspend fun logout()
}

