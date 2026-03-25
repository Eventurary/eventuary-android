package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens
import com.eventurary.core.providers.DateTimeProvider
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class IsTokensExpiredUseCaseImplTest {

    companion object {
        private const val CREATION_TIME = 1_000L
        private const val LIFE_SPAN = 1_000L
        private const val EXPIRE_TIME = CREATION_TIME + LIFE_SPAN
        private val buffer = IsTokensExpiredUseCaseImpl.expiryBufferMillis

        private val tokens = AuthTokens(
            accessToken = "access",
            refreshToken = "refresh",
            creationTime = CREATION_TIME,
            lifeSpan = LIFE_SPAN,
        )
    }

    private val mockDateTimeProvider = mockk<DateTimeProvider>()
    private val cut = IsTokensExpiredUseCaseImpl(mockDateTimeProvider)

    @Test
    fun `returns false when token is not expired and outside buffer`() {
        // GIVEN
        val currentTime = EXPIRE_TIME - buffer - 1
        every { mockDateTimeProvider.nowMillis } returns currentTime

        // WHEN
        val result = cut(tokens)

        // THEN
        assertFalse(result)
    }

    @Test
    fun `returns true when token is within buffer`() {
        // GIVEN
        val currentTime = EXPIRE_TIME - buffer + 1
        every { mockDateTimeProvider.nowMillis } returns currentTime

        // WHEN
        val result = cut(tokens)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `returns true when token is fully expired`() {
        // GIVEN
        val currentTime = EXPIRE_TIME + 1
        every { mockDateTimeProvider.nowMillis } returns currentTime

        // WHEN
        val result = cut(tokens)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `returns false when exactly at buffer boundary`() {
        // GIVEN
        val currentTime = EXPIRE_TIME - buffer
        every { mockDateTimeProvider.nowMillis } returns currentTime

        // WHEN
        val result = cut(tokens)

        // THEN
        assertFalse(result)
    }
}
