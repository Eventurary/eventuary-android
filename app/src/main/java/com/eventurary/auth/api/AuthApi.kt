package com.eventurary.auth.api

import com.eventurary.core.network.ApiResult
import com.eventurary.core.network.makeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.contentType

interface AuthApi {
    suspend fun login(email: String, password: String): ApiResult
    suspend fun register(email: String, password: String, name: String): ApiResult
    suspend fun logout(): ApiResult
    suspend fun isLoggedIn(): Boolean
}

class AuthApiImpl(
    private val client: HttpClient
) : AuthApi {

    companion object {
        const val LOGIN_PATH = "/login"
        const val REGISTER_PATH = "/register"
        const val LOGOUT_PATH = "/logout"
        const val ME_PATH = "/me"
    }

    override suspend fun login(email: String, password: String): ApiResult =
        client.makeRequest(
            path = LOGIN_PATH,
            method = HttpMethod.Post,
            body = mapOf("email" to email, "password" to password)
        )


    override suspend fun register(email: String, password: String, name: String): ApiResult =
        client.makeRequest(
            path = REGISTER_PATH,
            method = HttpMethod.Post,
            body = mapOf("email" to email, "password" to password, "name" to name)
        )


    override suspend fun logout(): ApiResult =
        client.makeRequest(
            path = LOGOUT_PATH,
            method = HttpMethod.Post
        )


    override suspend fun isLoggedIn(): Boolean =
        client.makeRequest(
            path = ME_PATH,
            method = HttpMethod.Get
        ) is ApiResult.Success
}
