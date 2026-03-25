package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.data.RefreshQueryParams
import com.eventurary.auth.repositories.TokensRepository
import com.eventurary.auth.services.AuthService
import com.eventurary.auth.services.RefreshServiceResult
import com.eventurary.core.providers.ConnectivityProvider
import kotlinx.coroutines.test.runTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.Test

class RefreshTokensUseCaseImplTest {

    companion object {
        private val tokens = AuthTokens("a", "r", 0L, 1000L)
        private val refreshQuery = RefreshQueryParams(tokens.refreshToken)
    }

    private val mockAuthService = mockk<AuthService>()
    private val mockIsTokensExpiredUseCase = mockk<IsTokensExpiredUseCase>()
    private val mockConnectivityProvider = mockk<ConnectivityProvider>()
    private val mockTokensRepository = mockk<TokensRepository>()

    private val cut = RefreshTokensUseCaseImpl(
        mockAuthService,
        mockIsTokensExpiredUseCase,
        mockConnectivityProvider,
        mockTokensRepository,
    )

    @Before
    fun setUp() {
        coEvery { mockTokensRepository.getTokens() } returns tokens
        coEvery { mockIsTokensExpiredUseCase(tokens) } returns true
        coEvery { mockConnectivityProvider.isInternetConnected() } returns true
    }

    @Test
    fun `returns LoggedOut when no tokens`() = runTest {
        // GIVEN
        coEvery { mockTokensRepository.getTokens() } returns null

        // WHEN
        val result = cut()

        // THEN
        assertEquals(RefreshTokensResult.LoggedOut, result)
        coVerify(exactly = 0) { mockAuthService.refresh(any()) }
    }

    @Test
    fun `returns StillValid when tokens not expired`() = runTest {
        // GIVEN
        coEvery { mockIsTokensExpiredUseCase(tokens) } returns false

        // WHEN
        val result = cut()

        // THEN
        assertEquals(RefreshTokensResult.StillValid(tokens), result)
        verify(exactly = 1) { mockIsTokensExpiredUseCase(tokens) }
        coVerify(exactly = 0) { mockAuthService.refresh(any()) }
    }

    @Test
    fun `returns Stale when tokens expired and no internet`() = runTest {
        // GIVEN
        coEvery { mockConnectivityProvider.isInternetConnected() } returns false

        // WHEN
        val result = cut()

        // THEN
        assertEquals(RefreshTokensResult.Stale(tokens), result)
        verify(exactly = 1) { mockIsTokensExpiredUseCase(tokens) }
        verify(exactly = 1) { mockConnectivityProvider.isInternetConnected() }
        coVerify(exactly = 0) { mockAuthService.refresh(any()) }
    }

    @Test
    fun `returns Refreshed when tokens expired and internet available and refresh succeeds`() = runTest {
        // GIVEN
        val refreshedTokens = tokens.copy(accessToken = "refreshed")

        coEvery { mockAuthService.refresh(refreshQuery) } returns RefreshServiceResult.Success(refreshedTokens)

        // WHEN
        val result = cut()

        // THEN
        verify(exactly = 1) { mockIsTokensExpiredUseCase(tokens) }
        verify(exactly = 1) { mockConnectivityProvider.isInternetConnected() }
        assertEquals(RefreshTokensResult.Refreshed(refreshedTokens), result)
    }

    @Test
    fun `returns Stale when auth service refresh fails`() = runTest {
        // GIVEN
        coEvery { mockAuthService.refresh(refreshQuery) } returns RefreshServiceResult.Failure

        // WHEN
        val result = cut()

        // THEN
        verify(exactly = 1) { mockIsTokensExpiredUseCase(tokens) }
        verify(exactly = 1) { mockConnectivityProvider.isInternetConnected() }
        assertEquals(RefreshTokensResult.Stale(tokens), result)
    }

    @Test
    fun `returns LoggedOut when auth service refresh logs out`() = runTest {
        // GIVEN
        coEvery { mockAuthService.refresh(refreshQuery) } returns RefreshServiceResult.LoggedOut

        // WHEN
        val result = cut()

        // THEN
        verify(exactly = 1) { mockIsTokensExpiredUseCase(tokens) }
        verify(exactly = 1) { mockConnectivityProvider.isInternetConnected() }
        assertEquals(RefreshTokensResult.LoggedOut, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `concurrent refreshes are mutex locked`() = runTest {
        // GIVEN
        val gate = CompletableDeferred<Unit>()

        coEvery { mockAuthService.refresh(refreshQuery) } coAnswers {
            gate.await()
            RefreshServiceResult.Success(tokens)
        }

        // WHEN
        launch { cut() }
        launch { cut() }
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 1) { mockAuthService.refresh(any()) }

        // WHEN - Ensure both calls complete after completing first refresh
        gate.complete(Unit)
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 2) { mockAuthService.refresh(any()) }
    }
}
