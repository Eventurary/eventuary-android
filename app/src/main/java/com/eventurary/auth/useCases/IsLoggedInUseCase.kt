package com.eventurary.auth.useCases

interface IsLoggedInUseCase {
    suspend operator fun invoke(): Boolean
}

class IsLoggedInUseCaseImpl(
    private val getAuthTokensUseCase: GetAuthTokensUseCase
) : IsLoggedInUseCase {

    override suspend operator fun invoke(): Boolean {
        val tokens = getAuthTokensUseCase()
        return tokens != null
    }
}
