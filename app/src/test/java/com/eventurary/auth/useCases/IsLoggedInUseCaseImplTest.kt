package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class IsLoggedInUseCaseImplTest {

    private val mockGetAuthTokensUseCase = mockk<GetAuthTokensUseCase>()
    private val cut = IsLoggedInUseCaseImpl(mockGetAuthTokensUseCase)

    @Test
    fun `returns true when tokens exist`() = runTest {
        // GIVEN
        val tokens = AuthTokens("a", "r", 0L, 0L)
        coEvery { mockGetAuthTokensUseCase() } returns tokens

        // WHEN
        val result = cut()

        // THEN
        assertTrue(result)
    }

    @Test
    fun `returns false when tokens are null`() = runTest {
        // GIVEN
        coEvery { mockGetAuthTokensUseCase() } returns null

        // WHEN
        val result = cut()

        // THEN
        assertFalse(result)
    }
}
