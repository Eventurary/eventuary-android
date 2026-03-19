package com.eventurary.core.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {

    @Serializable
    data object OnboardingWall: Route

    @Serializable
    data object AuthGraph: Route {

        @Serializable
        data object Login: Route

        @Serializable
        data object Register: Route
    }

    @Serializable
    data object EventsGraph: Route {

        @Serializable
        data object EventList: Route

        @Serializable
        data class EventDetails(val eventId: String): Route
    }
}
