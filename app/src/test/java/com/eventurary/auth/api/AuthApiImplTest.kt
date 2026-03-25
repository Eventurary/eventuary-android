package com.eventurary.auth.api

import com.eventurary.auth.data.AuthTokensDTO
import com.eventurary.auth.data.LoginQueryParams
import com.eventurary.auth.data.RefreshQueryParams
import com.eventurary.auth.data.RegisterQueryParams
import com.eventurary.core.mocks.createFakeClient
import com.eventurary.core.mocks.respondOk
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AuthApiImplTest {

    companion object {
        private val tokensDto = AuthTokensDTO(
            accessToken = "access",
            refreshToken = "refresh",
            lifeSpan = 2000L,
        )
    }

    private lateinit var fakeClient: HttpClient
    private lateinit var cut: AuthApiImpl

    @Before
    fun setUp() {
        fakeClient = createFakeClient { _ -> respondOk(tokensDto) }
        cut = AuthApiImpl(fakeClient)
    }

    @Test
    fun `login returns success on 200`() = runTest {
        // WHEN
        val result = cut.login(mockk<LoginQueryParams>(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.Success(tokensDto), result)
    }

    @Test
    fun `register returns success on 200`() = runTest {
        // WHEN
        val result = cut.register(mockk<RegisterQueryParams>(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.Success(tokensDto), result)
    }

    @Test
    fun `refresh returns success on 200`() = runTest {
        // WHEN
        val result = cut.refresh(mockk<RefreshQueryParams>(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.Success(tokensDto), result)
    }

    @Test
    fun `returns APIError on 401`() = runTest {
        // GIVEN
        fakeClient = createFakeClient { respond("", HttpStatusCode.Unauthorized) }
        cut = AuthApiImpl(fakeClient)

        // WHEN
        val result = cut.login(mockk(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.APIError(HttpStatusCode.Unauthorized), result)
    }

    @Test
    fun `returns APIError on 400`() = runTest {
        // GIVEN
        fakeClient = createFakeClient { respond("", HttpStatusCode.BadRequest) }
        cut = AuthApiImpl(fakeClient)

        // WHEN
        val result = cut.login(mockk(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.APIError(HttpStatusCode.BadRequest), result)
    }

    @Test
    fun `returns APIError on 500`() = runTest {
        // GIVEN
        fakeClient = createFakeClient { respond("", HttpStatusCode.InternalServerError) }
        cut = AuthApiImpl(fakeClient)

        // WHEN
        val result = cut.login(mockk(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.APIError(HttpStatusCode.InternalServerError), result)
    }

    @Test
    fun `returns APIError on unexpected status`() = runTest {
        // GIVEN
        fakeClient = createFakeClient { respond("", HttpStatusCode.Conflict) }
        cut = AuthApiImpl(fakeClient)

        // WHEN
        val result = cut.login(mockk(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.APIError(HttpStatusCode.Conflict), result)
    }

    @Test
    fun `returns ParsingError when body cannot be parsed`() = runTest {
        // GIVEN
        fakeClient = createFakeClient {
            respond(
                content = "invalid json",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        cut = AuthApiImpl(fakeClient)

        // WHEN
        val result = cut.login(mockk(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.ParsingError, result)
    }

    @Suppress("TooGenericExceptionThrown")
    @Test
    fun `returns NetworkError when request fails`() = runTest {
        // GIVEN
        fakeClient = HttpClient(MockEngine { throw RuntimeException("Network failure") })
        cut = AuthApiImpl(fakeClient)

        // WHEN
        val result = cut.login(mockk(relaxed = true))

        // THEN
        assertEquals(AuthApiResult.NetworkError, result)
    }

    @Test
    fun `login calls correct endpoint with real params`() = runTest {
        // GIVEN
        val loginParams = LoginQueryParams(
            email = "john@email.com",
            password = "johnspassword",
        )

        var capturedRequest: HttpRequestData? = null

        fakeClient = createFakeClient { request ->
            capturedRequest = request
            respondOk(tokensDto)
        }

        cut = AuthApiImpl(fakeClient)

        // WHEN
        cut.login(loginParams)

        // THEN
        val request = capturedRequest!!

        assertEquals("/login", request.url.encodedPath)
        assertEquals("john@email.com", request.url.parameters["email"])
        assertEquals("johnspassword", request.url.parameters["password"])
    }

    @Test
    fun `register calls correct endpoint with real params`() = runTest {
        // GIVEN
        val registerParams = RegisterQueryParams(
            username = "john",
            email = "john@email.com",
            password = "johnspassword",
        )

        var capturedRequest: HttpRequestData? = null

        fakeClient = createFakeClient { request ->
            capturedRequest = request
            respondOk(tokensDto)
        }

        cut = AuthApiImpl(fakeClient)

        // WHEN
        cut.register(registerParams)

        // THEN
        val request = capturedRequest!!

        assertEquals("/register", request.url.encodedPath)
        assertEquals("john", request.url.parameters["username"])
        assertEquals("john@email.com", request.url.parameters["email"])
        assertEquals("johnspassword", request.url.parameters["password"])
    }

    @Test
    fun `refresh calls correct endpoint with real params`() = runTest {
        // GIVEN
        val refreshParams = RefreshQueryParams(
            refreshToken = "refresh-token-123"
        )

        var capturedRequest: HttpRequestData? = null

        fakeClient = createFakeClient { request ->
            capturedRequest = request
            respondOk(tokensDto)
        }

        cut = AuthApiImpl(fakeClient)

        // WHEN
        cut.refresh(refreshParams)

        // THEN
        val request = capturedRequest!!

        assertEquals("/refresh", request.url.encodedPath)
        assertEquals("refresh-token-123", request.url.parameters["refresh"])
    }
}
