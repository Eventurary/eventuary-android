package com.eventurary.auth.useCases

import com.eventurary.auth.services.AuthService

interface LogoutUseCase {
    suspend operator fun invoke()
}

class LogoutUseCaseImpl(
    private val authService: AuthService
) : LogoutUseCase {

    override suspend operator fun invoke() {
        authService.logout()
    }
}
