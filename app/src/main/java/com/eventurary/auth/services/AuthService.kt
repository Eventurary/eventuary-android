package com.eventurary.auth.services

import com.eventurary.auth.data.LoginRequest
import com.eventurary.auth.data.RegisterRequest
import com.eventurary.auth.data.AuthTokens

sealed class AuthResult {
    data class Success(val tokens: AuthTokens) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class RefreshResult {
    data class Success(val tokens: AuthTokens) : RefreshResult()
    object Failure : RefreshResult()
    object LoggedOut : RefreshResult()
}

interface AuthService {
    suspend fun login(loginRequest: LoginRequest): AuthResult
    suspend fun register(registerRequest: RegisterRequest): AuthResult
    suspend fun refresh(refreshToken: String): RefreshResult
    suspend fun logout()
}

