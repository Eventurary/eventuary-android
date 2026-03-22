package com.eventurary.auth.repository

import com.eventurary.auth.api.AuthApi
import com.eventurary.core.network.ApiResult
import com.eventurary.core.network.PersistentSessionCookiesStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String, name: String): AuthResult
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val persistentSessionCookiesStorage: PersistentSessionCookiesStorage,
) : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    override suspend fun login(email: String, password: String): AuthResult {
        val result = authApi.login(email, password)

        return when (result) {
            is ApiResult.Success -> {
                _isLoggedIn.value = true
                AuthResult.Success
            }

            is ApiResult.Error -> {
                // TODO: More descriptive
                AuthResult.Error("Invalid email or password")
            }
        }
    }

    override suspend fun register(email: String, password: String, name: String): AuthResult {
        val result = authApi.register(email, password, name)

        return when (result) {
            is ApiResult.Success -> {
                _isLoggedIn.value = true
                AuthResult.Success
            }

            is ApiResult.Error -> {
                // TODO: More descriptive
                AuthResult.Error("Registration failed")
            }
        }
    }

    override suspend fun logout() {
        // TODO: Clear cookie self?
        val result = authApi.logout()

        _isLoggedIn.value = false  // Update login state on logout
    }

    override suspend fun isLoggedIn(): Boolean {
        return _isLoggedIn.value || authApi.isLoggedIn()
    }
}