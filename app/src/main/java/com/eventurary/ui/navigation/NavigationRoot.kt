package com.eventurary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.eventurary.ui.screens.EventListScreen
import com.eventurary.ui.screens.EventDetailsScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.EventList::class, Route.EventList.serializer())
                    subclass(Route.EventDetails::class, Route.EventDetails.serializer())
                }
            }
        },
        Route.EventList
    )

    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is Route.EventList -> {
                    NavEntry(key) {
                        EventListScreen(
                            onEventClick = {
                                backStack.add(Route.EventDetails(it))
                            }
                        )
                    }
                }

                is Route.EventDetails -> {
                    NavEntry(key) {
                        EventDetailsScreen(
                            event = key.eventId,
                        )
                    }
                }

                else -> error("Unknown NavKey: $key")
            }
        },
    )
}