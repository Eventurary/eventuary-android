package com.eventurary.auth.useCases

import com.eventurary.auth.data.AuthTokens

sealed class RefreshTokensResult {
    data class Refreshed(val freshTokens: AuthTokens) : RefreshTokensResult()
    data class StillValid(val freshTokens: AuthTokens) : RefreshTokensResult()
    data class Stale(val staleTokens: AuthTokens) : RefreshTokensResult()
    object LoggedOut : RefreshTokensResult()

    companion object {
        fun RefreshTokensResult.getTokens()  =
            when (this) {
                is Refreshed -> freshTokens
                is StillValid -> freshTokens
                is Stale -> staleTokens
                is LoggedOut -> null
            }

        fun RefreshTokensResult.getFreshTokens(): AuthTokens? =
            when (this) {
                is Refreshed -> freshTokens
                is StillValid -> freshTokens
                is Stale -> null
                is LoggedOut -> null
            }
    }
}
