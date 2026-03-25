package com.eventurary.auth.api

import com.eventurary.auth.data.AuthTokensDTO
import io.ktor.http.HttpStatusCode

sealed class AuthApiResult {
    data class Success(val response: AuthTokensDTO) : AuthApiResult()
    data class APIError(val status: HttpStatusCode) : AuthApiResult()
    object NetworkError : AuthApiResult()
    object ParsingError : AuthApiResult()
}
