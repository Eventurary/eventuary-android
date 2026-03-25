package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.auth.repositories.TokensRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HasAuthTokenAsFlowUseCaseImplTest {

    companion object {
        private val tokens = AuthTokens(
            accessToken = "access",
            refreshToken = "refresh",
            creationTime = 1000L,
            lifeSpan = 1000L,
        )
    }

    private val mockTokensRepository = mockk<TokensRepository>()
    private val cut = HasAuthTokenAsFlowUseCaseImpl(tokensRepository = mockTokensRepository)

    @Test
    fun `emits false when tokens are null`() = runTest {
        // GIVEN
        every { mockTokensRepository.authTokensFlow } returns flowOf(null)

        // WHEN
        val result = cut().take(1).toList()

        // THEN
        assertEquals(listOf(false), result)
    }

    @Test
    fun `emits true when tokens exist`() = runTest {
        // GIVEN
        every { mockTokensRepository.authTokensFlow } returns flowOf(tokens)

        // WHEN
        val result = cut().take(1).toList()

        // THEN
        assertEquals(listOf(true), result)
    }

    @Test
    fun `emits correct sequence when tokens change`() = runTest {
        // GIVEN
        every { mockTokensRepository.authTokensFlow } returns flowOf(
            null,
            tokens,
            null
        )

        // WHEN
        val result = cut().toList()

        // THEN
        assertEquals(
            listOf(false, true, false),
            result
        )
    }

    @Test
    fun `does not emit duplicates due to distinctUntilChanged`() = runTest {
        // GIVEN
        every { mockTokensRepository.authTokensFlow } returns flowOf(
            null,
            tokens,
            tokens.copy(accessToken = "duplicate"),
            null,
            null,
        )

        // WHEN
        val result = cut().toList()

        // THEN
        assertEquals(
            listOf(false, true, false),
            result
        )
    }
}
