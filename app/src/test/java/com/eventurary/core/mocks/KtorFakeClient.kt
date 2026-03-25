package com.eventurary.core.mocks

import com.eventurary.auth.data.AuthTokensDTO
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val defaultJson = Json { ignoreUnknownKeys = true }

/**
 * Creates a fake HttpClient using Ktor's MockEngine.
 */
fun createFakeClient(
    json: Json = defaultJson,
    handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
): HttpClient {
    return HttpClient(MockEngine) {
        engine { addHandler { request -> handler(request) } }
        install(ContentNegotiation) { json(json) }
    }
}

/**
 * Helper to return 200 OK with a serialized AuthTokensDTO body.
 */
fun MockRequestHandleScope.respondOk(body: AuthTokensDTO, json: Json = defaultJson): HttpResponseData {
    return respond(
        content = json.encodeToString(AuthTokensDTO.serializer(), body),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}

/**
 * Helper to return an error response.
 */
fun MockRequestHandleScope.respondError(
    status: HttpStatusCode,
    content: String = "",
): HttpResponseData {
    return respond(
        content = content,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}
