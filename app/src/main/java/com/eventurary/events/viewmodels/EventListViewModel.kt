package com.eventurary.events.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventListViewModel: ViewModel() {

    companion object {
        private const val MAX_EVENTS = 100
    }

    private val _events: MutableStateFlow<List<String>>
        get() = MutableStateFlow(
            (1..MAX_EVENTS).map { "Event $it" }
        )
    val events = _events.asStateFlow()
}
