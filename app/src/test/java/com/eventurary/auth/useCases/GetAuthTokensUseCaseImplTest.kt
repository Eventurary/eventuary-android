package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.repositories.TokensRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertNull

class GetAuthTokensUseCaseImplTest {

    companion object {
        private val tokens = AuthTokens(
            accessToken = "access",
            refreshToken = "refresh",
            creationTime = 1000L,
            lifeSpan = 1000L,
        )
    }

    private val mockTokensRepository = mockk<TokensRepository>()
    private val mockIsTokensExpiredUseCase = mockk<IsTokensExpiredUseCase>()
    private val mockRefreshTokensUseCase = mockk<RefreshTokensUseCase>()

    private val cut = GetAuthTokensUseCaseImpl(
        tokensRepository = mockTokensRepository,
        isTokensExpiredUseCase = mockIsTokensExpiredUseCase,
        refreshTokensUseCase = mockRefreshTokensUseCase,
    )

    @Before
    fun setUp() {
        coEvery { mockTokensRepository.getTokens() } returns tokens
        every { mockIsTokensExpiredUseCase(tokens) } returns true
    }

    @Test
    fun `returns null when no tokens stored`() = runTest {
        // GIVEN
        coEvery { mockTokensRepository.getTokens() } returns null

        // WHEN
        val result = cut()

        // THEN
        assertNull(result)
        coVerify (exactly = 0) { mockRefreshTokensUseCase.invoke() }
    }

    @Test
    fun `returns tokens when not expired`() = runTest {
        // GIVEN
        every { mockIsTokensExpiredUseCase(tokens) } returns false

        // WHEN
        val result = cut()

        // THEN
        assertEquals(tokens, result)
        coVerify(exactly = 0) { mockRefreshTokensUseCase.invoke() }
    }

    @Test
    fun `refreshes tokens when expired and returns refreshed tokens`() = runTest {
        // GIVEN
        val refreshedTokens = tokens.copy(accessToken = "newAccess")
        coEvery { mockRefreshTokensUseCase.invoke() } returns RefreshTokensResult.Refreshed(refreshedTokens)

        // WHEN
        val result = cut()

        // THEN
        assertEquals(refreshedTokens, result)
        coVerify(exactly = 1) { mockRefreshTokensUseCase.invoke() }
    }

    @Test
    fun `returns null when logged out`() = runTest {
        // GIVEN
        coEvery { mockRefreshTokensUseCase.invoke() } returns RefreshTokensResult.LoggedOut

        // WHEN
        val result = cut()

        // THEN
        assertNull(result)
        coVerify(exactly = 1) { mockRefreshTokensUseCase.invoke() }
    }

    @Test
    fun `returns tokens when still valid`() = runTest {
        // GIVEN
        coEvery { mockRefreshTokensUseCase.invoke() } returns RefreshTokensResult.StillValid(tokens)

        // WHEN
        val result = cut()

        // THEN
        assertEquals(tokens, result)
        coVerify(exactly = 1) { mockRefreshTokensUseCase.invoke() }
    }

    @Test
    fun `returns tokens when stale`() = runTest {
        // GIVEN
        coEvery { mockRefreshTokensUseCase.invoke() } returns RefreshTokensResult.Stale(tokens)

        // WHEN
        val result = cut()

        // THEN
        assertEquals(tokens, result)
        coVerify(exactly = 1) { mockRefreshTokensUseCase.invoke() }
    }
}
