package com.eventurary.auth.api

import com.eventurary.auth.data.AuthTokensDTO
import com.eventurary.auth.data.LoginQueryParams
import com.eventurary.auth.data.RefreshQueryParams
import com.eventurary.auth.data.RegisterQueryParams
import com.eventurary.core.network.QueryParams
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
    suspend fun login(loginQueryParams: LoginQueryParams): AuthApiResult
    suspend fun register(registerQueryParams: RegisterQueryParams): AuthApiResult
    suspend fun refresh(refreshQueryParams: RefreshQueryParams): AuthApiResult
}

class AuthApiImpl(private val bffClient: HttpClient) : AuthApi {

    companion object {
        const val LOGIN_PATH = "/login"
        const val REGISTER_PATH = "/register"
        const val REFRESH_PATH = "/refresh"
    }

    override suspend fun login(loginQueryParams: LoginQueryParams): AuthApiResult {
        return sendRequest(
            url = LOGIN_PATH,
            queryParams = loginQueryParams,
        )
    }

    override suspend fun register(registerQueryParams: RegisterQueryParams): AuthApiResult {
        return sendRequest(
            url = REGISTER_PATH,
            queryParams = registerQueryParams,
        )
    }

    override suspend fun refresh(refreshQueryParams: RefreshQueryParams): AuthApiResult {
        return sendRequest(
            url = REFRESH_PATH,
            queryParams = refreshQueryParams,
        )
    }

    private suspend fun sendRequest(url: String, queryParams: QueryParams): AuthApiResult {
        return runCatching {
            val response = bffClient.post(url) {
                url {
                    queryParams.applyParams(it)
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
