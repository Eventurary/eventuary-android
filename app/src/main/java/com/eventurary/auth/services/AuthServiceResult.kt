package com.eventurary.auth.services

import com.eventurary.auth.data.AuthTokens

sealed class AuthServiceResult {
    data class Success(val tokens: AuthTokens) : AuthServiceResult()
    data class Error(val message: String) : AuthServiceResult()
}
