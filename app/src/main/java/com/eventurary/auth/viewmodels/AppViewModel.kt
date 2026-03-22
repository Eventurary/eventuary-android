package com.eventurary.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventurary.auth.useCases.HasAuthTokenAsFlowUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed class StartDestination {
    object Loading : StartDestination()
    object App : StartDestination()
    object Login : StartDestination()

}

class AppViewModel(
    private val hasAuthTokenAsFlowUseCase: HasAuthTokenAsFlowUseCase
) : ViewModel() {

    private companion object {
        private const val STATE_FLOW_TIMEOUT = 5000L
    }

    val startDestination: StateFlow<StartDestination> =
        hasAuthTokenAsFlowUseCase()
            .map { hasAuthToken ->
                if (hasAuthToken) StartDestination.App else StartDestination.Login
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT),
                initialValue = StartDestination.Loading
            )
}
