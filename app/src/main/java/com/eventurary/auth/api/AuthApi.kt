package com.eventurary.auth.api

import com.eventurary.auth.data.AuthTokensDTO
import com.eventurary.auth.data.LoginRequest
import com.eventurary.auth.data.RefreshRequest
import com.eventurary.auth.data.RegisterRequest
import com.eventurary.auth.mappers.LoginQueryMapper
import com.eventurary.auth.mappers.RefreshQueryMapper
import com.eventurary.auth.mappers.RegisterQueryMapper
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

sealed class AuthApiResult {
    data class Success(val response: AuthTokensDTO) : AuthApiResult()
    data class APIError(val status: HttpStatusCode) : AuthApiResult()
    object NetworkError : AuthApiResult()
    object ParsingError : AuthApiResult()
}

interface AuthApi {
    suspend fun login(loginRequest: LoginRequest): AuthApiResult
    suspend fun register(registerRequest: RegisterRequest): AuthApiResult
    suspend fun refresh(refreshRequest: RefreshRequest): AuthApiResult
}

// TODO: Path query params or body?
// TODO: Add type to send, ie if refresh or login type attempt
class AuthApiImpl(
    private val client: HttpClient,
    private val loginQueryMapper: LoginQueryMapper,
    private val registerQueryMapper: RegisterQueryMapper,
    private val refreshQueryMapper: RefreshQueryMapper,
) : AuthApi {

    companion object {
        const val LOGIN_PATH = "/login"
        const val REGISTER_PATH = "/register"
        const val REFRESH_PATH = "/refresh"
    }

    override suspend fun login(loginRequest: LoginRequest): AuthApiResult {
        return sendRequest(
            url = LOGIN_PATH,
            queryParams = loginQueryMapper.toQueryParams(loginRequest),
        )
    }

    override suspend fun register(registerRequest: RegisterRequest): AuthApiResult {
        return sendRequest(
            url = REGISTER_PATH,
            queryParams = registerQueryMapper.toQueryParams(registerRequest),
        )
    }

    override suspend fun refresh(refreshRequest: RefreshRequest): AuthApiResult {
        return sendRequest(
            url = REFRESH_PATH,
            queryParams = refreshQueryMapper.toQueryParams(refreshRequest),
        )
    }

    private suspend fun sendRequest(url: String, queryParams: Map<String, String>): AuthApiResult {
        return runCatching {
            val response = client.post(url) {
                url {
                    queryParams.forEach { (key, value) ->
                        parameters.append(key, value)
                    }
                }
            }

            handleResponse(response)
        }.getOrElse { e ->
            Napier.e(e) { "Network error during request" }
            AuthApiResult.NetworkError
        }
    }

    private suspend fun handleResponse(response: HttpResponse): AuthApiResult {
        return when (response.status) {
            HttpStatusCode.OK -> {
                tryParseBody(response)
            }
            HttpStatusCode.Unauthorized -> {
                Napier.e { "Invalid username or password" }
                AuthApiResult.APIError(response.status)
            }
            HttpStatusCode.BadRequest -> {
                Napier.e { "Bad request. Please check the input fields." }
                AuthApiResult.APIError(response.status)
            }
            HttpStatusCode.InternalServerError -> {
                Napier.e { "Something went wrong on the server side." }
                AuthApiResult.APIError(response.status)
            }
            HttpStatusCode.NotFound -> {
                Napier.e { "Requested resource not found." }
                AuthApiResult.APIError(response.status)
            }
            else -> {
                Napier.e { "Unexpected error occurred: ${response.status}" }
                AuthApiResult.APIError(response.status)
            }
        }
    }

    private suspend fun tryParseBody(response: HttpResponse): AuthApiResult {
        return try {
            val result = response.body<AuthTokensDTO>()
            AuthApiResult.Success(result)
        } catch(e: NoTransformationFoundException) {
            Napier.e(e) { "Cannot cast login response to valid LoginResultDTO" }
            AuthApiResult.ParsingError
        }
    }
}
