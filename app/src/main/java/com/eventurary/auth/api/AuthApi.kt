package com.eventurary.auth.api

import com.eventurary.auth.data.LoginQueryParams
import com.eventurary.auth.data.RefreshQueryParams
import com.eventurary.auth.data.RegisterQueryParams

interface AuthApi {
    suspend fun login(loginQueryParams: LoginQueryParams): AuthApiResult
    suspend fun register(registerQueryParams: RegisterQueryParams): AuthApiResult
    suspend fun refresh(refreshQueryParams: RefreshQueryParams): AuthApiResult
}
