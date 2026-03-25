package com.eventurary.auth.useCases

import com.eventurary.auth.services.AuthService
import io.mockk.coJustRun
import kotlinx.coroutines.test.runTest
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test

class LogoutUseCaseImplTest {

    private val mockAuthService = mockk<AuthService>()
    private val cut = LogoutUseCaseImpl(mockAuthService)

    @Test
    fun `invokes authService logout`() = runTest {
        // GIVEN
        coJustRun { mockAuthService.logout() }

        // WHEN
        cut()

        // THEN
        coVerify(exactly = 1) { mockAuthService.logout() }
    }
}
