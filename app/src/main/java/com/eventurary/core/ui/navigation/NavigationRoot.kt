package com.eventurary.core.ui.navigation

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
import com.eventurary.auth.AuthDisplay
import com.eventurary.auth.ui.screens.OnboardingWallScreen
import com.eventurary.events.EventDisplay
import com.eventurary.testing.TestingDisplay

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(Route.OnboardingWall)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = rootNavEntryProvider(backStack),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
    )
}

private fun rootNavEntryProvider(backStack: NavBackStack<NavKey>): (NavKey) -> NavEntry<NavKey> =
    entryProvider {
        entry<Route.OnboardingWall> {
            OnboardingWallScreen(
                onContinueClicked = {
                    backStack.add(Route.AuthGraph)
                },
                onTestingClicked = {
                    replaceDisplay(Route.TestingGraph, backStack)
                }
            )
        }

        entry<Route.AuthGraph> {
            AuthDisplay(
                onSuccess = {
                    replaceDisplay(Route.EventsGraph, backStack)
                }
            )
        }

        entry<Route.EventsGraph> {
            EventDisplay()
        }

        entry<Route.TestingGraph> {
            TestingDisplay()
        }
    }

private fun replaceDisplay(key: NavKey, backStack: NavBackStack<NavKey>) {
    backStack.clear()
    backStack.add(key)
}
