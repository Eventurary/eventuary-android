package com.eventurary.events

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.eventurary.core.ui.navigation.Route
import com.eventurary.events.ui.screens.EventDetailsScreen
import com.eventurary.events.ui.screens.EventListScreen

@Composable
fun EventDisplay(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(Route.EventsGraph.EventList)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = eventNavEntryProvider(backStack),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
    )
}

private fun eventNavEntryProvider(
    backStack: NavBackStack<NavKey>
): (NavKey) -> NavEntry<NavKey> =
    entryProvider {
        entry<Route.EventsGraph.EventList> {
            EventListScreen(
                onEventClick = { eventId ->
                    backStack.add(Route.EventsGraph.EventDetails(eventId))
                }
            )
        }

        entry<Route.EventsGraph.EventDetails> { key ->
            EventDetailsScreen(event = key.eventId)
        }
    }
