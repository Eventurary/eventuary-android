package com.eventurary.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventListViewModel: ViewModel() {

    private val _events = MutableStateFlow(
        (1..100).map { "Event $it" }
    )
    val events = _events.asStateFlow()
}