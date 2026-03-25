package com.eventurary.auth.services

import com.eventurary.auth.data.AuthTokens

sealed class RefreshServiceResult {
    data class Success(val tokens: AuthTokens) : RefreshServiceResult()
    object Failure : RefreshServiceResult()
    object LoggedOut : RefreshServiceResult()
}
