package com.eventurary.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventurary.auth.data.LoginQueryParams
import com.eventurary.auth.data.RegisterQueryParams
import com.eventurary.auth.services.AuthServiceResult
import com.eventurary.auth.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> get() = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val request = LoginQueryParams(
                email = email,
                password = password,
            )

            val result = authService.login(request)
            _uiState.value = when (result) {
                is AuthServiceResult.Success -> AuthUiState.Success
                is AuthServiceResult.Error -> AuthUiState.Error(result.message)
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val request = RegisterQueryParams(
                username = name,
                email = email,
                password = password,
            )

            val result = authService.register(request)
            _uiState.value = when (result) {
                is AuthServiceResult.Success -> AuthUiState.Success
                is AuthServiceResult.Error -> AuthUiState.Error(result.message)
            }
        }
    }
}
