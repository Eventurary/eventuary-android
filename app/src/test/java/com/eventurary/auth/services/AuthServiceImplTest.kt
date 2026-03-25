package com.eventurary.auth.services

import com.eventurary.auth.api.AuthApi
import com.eventurary.auth.api.AuthApiResult
import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.data.AuthTokensDTO
import com.eventurary.auth.data.LoginQueryParams
import com.eventurary.auth.data.RefreshQueryParams
import com.eventurary.auth.data.RegisterQueryParams
import com.eventurary.auth.mappers.AuthTokensMapper
import com.eventurary.auth.repositories.TokensRepository
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AuthServiceImplTest {

    companion object {
        private val loginParams = LoginQueryParams(
            email = "email",
            password = "password"
        )
        private val registerParams = RegisterQueryParams(
            username = "username",
            email = "email",
            password = "password",
        )
        private val refreshParams = RefreshQueryParams(
            refreshToken = "refresh"
        )

        private val tokensDto = AuthTokensDTO(
            accessToken = "access",
            refreshToken = "refresh",
            lifeSpan = 1000L,
        )
        private val tokens = AuthTokens(
            accessToken = "access",
            refreshToken = "refresh",
            creationTime = 1000L,
            lifeSpan = 1000L,
        )
    }

    private val mockAuthApi = mockk<AuthApi>()
    private val mockTokensRepository = mockk<TokensRepository>()
    private val mockAuthTokensMapper = mockk<AuthTokensMapper>()

    private val cut = AuthServiceImpl(
        authApi = mockAuthApi,
        tokensRepository = mockTokensRepository,
        authTokensMapper = mockAuthTokensMapper,
    )

    @Before
    fun setUp() {
        every { mockAuthTokensMapper.map(tokensDto) } returns tokens
        coJustRun { mockTokensRepository.saveTokens(any()) }
        coJustRun { mockTokensRepository.clearTokens() }

    }

    @Test
    fun `login success saves tokens and returns success`() = runTest {
        // GIVEN
        coEvery { mockAuthApi.login(loginParams) } returns AuthApiResult.Success(tokensDto)

        // WHEN
        val result = cut.login(loginParams)

        // THEN
        coVerify (exactly = 1) { mockTokensRepository.saveTokens(tokens) }
        assertTrue(result is AuthServiceResult.Success)
        assertEquals(tokens, (result as AuthServiceResult.Success).tokens)
    }

    @Test
    fun `register success behaves like login`() = runTest {
        // GIVEN
        coEvery { mockAuthApi.register(registerParams) } returns AuthApiResult.Success(tokensDto)

        // WHEN
        val result = cut.register(registerParams)

        // THEN
        coVerify(exactly = 1) { mockTokensRepository.saveTokens(tokens) }
        assertTrue(result is AuthServiceResult.Success)
        assertEquals(tokens, (result as AuthServiceResult.Success).tokens)
    }

    @Test
    fun `login failure returns error`() = runTest {
        // GIVEN
        coEvery { mockAuthApi.login(loginParams) } returns AuthApiResult.NetworkError

        // WHEN
        val result = cut.login(loginParams)

        // THEN
        coVerify(exactly = 0) { mockTokensRepository.saveTokens(any()) }
        assertTrue(result is AuthServiceResult.Error)
    }

    @Test
    fun `refresh success saves tokens and returns success`() = runTest {
        // GIVEN
        coEvery { mockAuthApi.refresh(refreshParams) } returns AuthApiResult.Success(tokensDto)

        // WHEN
        val result = cut.refresh(refreshParams)

        // THEN
        coVerify(exactly = 1) { mockTokensRepository.saveTokens(tokens) }
        assertTrue(result is RefreshServiceResult.Success)
        assertEquals(tokens, (result as RefreshServiceResult.Success).tokens)
    }

    @Test
    fun `refresh api error with 401 clears tokens and logs out`() = runTest {
        // GIVEN
        val error = AuthApiResult.APIError(HttpStatusCode.Unauthorized)
        coEvery { mockAuthApi.refresh(refreshParams) } returns error

        // WHEN
        val result = cut.refresh(refreshParams)

        // THEN
        coVerify(exactly = 1) { mockTokensRepository.clearTokens() }
        assertEquals(RefreshServiceResult.LoggedOut, result)
    }

    @Test
    fun `refresh api error with 500 does not clear tokens`() = runTest {
        // GIVEN
        val error = AuthApiResult.APIError(HttpStatusCode.InternalServerError)
        coEvery { mockAuthApi.refresh(refreshParams) } returns error

        // WHEN
        val result = cut.refresh(refreshParams)

        // THEN
        coVerify(exactly = 0) { mockTokensRepository.clearTokens() }
        assertEquals(RefreshServiceResult.Failure, result)
    }

    @Test
    fun `refresh non api error returns failure`() = runTest {
        // GIVEN
        coEvery { mockAuthApi.refresh(refreshParams) } returns AuthApiResult.NetworkError

        // WHEN
        val result = cut.refresh(refreshParams)

        // THEN
        assertEquals(RefreshServiceResult.Failure, result)
    }

    @Test
    fun `logout clears tokens`() = runTest {
        // WHEN
        cut.logout()

        // THEN
        coVerify(exactly = 1) { mockTokensRepository.clearTokens() }
    }
}
