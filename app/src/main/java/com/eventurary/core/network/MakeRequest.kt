package com.eventurary.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.client.call.body

suspend fun HttpClient.makeRequest(
    path: String,
    method: HttpMethod,
    body: Map<String, String> = emptyMap()
): ApiResult =
    runCatching {
        val response = this.request(path) {
            this.method = method
            contentType(ContentType.Application.Json)
            if (body.isNotEmpty()) setBody(body)
        }

        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created -> ApiResult.Success
            else -> {
                val errorBody = runCatching {
                    response.body<String>()
                }.getOrElse {
                    "No error body"
                }
                ApiResult.Error(response.status.value, errorBody)
            }
        }
    }.getOrElse {
        ApiResult.Error(-1, "Error making request: ${it.localizedMessage}")
    }

