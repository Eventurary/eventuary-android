package com.eventurary.auth.services

import com.eventurary.auth.data.LoginQueryParams
import com.eventurary.auth.data.RegisterQueryParams
import com.eventurary.auth.data.RefreshQueryParams

interface AuthService {
    suspend fun login(loginQueryParams: LoginQueryParams): AuthServiceResult
    suspend fun register(registerQueryParams: RegisterQueryParams): AuthServiceResult
    suspend fun refresh(refreshQueryParams: RefreshQueryParams): RefreshServiceResult
    suspend fun logout()
}

