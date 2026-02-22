package com.eventurary.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {

    @Serializable
    data object EventList: Route

    @Serializable
    data class EventDetails(val eventId: String): Route
}