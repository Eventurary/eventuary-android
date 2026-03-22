package com.eventurary.testing

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.eventurary.core.ui.navigation.Route.TestingGraph.ComponentScreen
import com.eventurary.testing.ui.screens.TestingScreen

@Composable
fun TestingDisplay(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(ComponentScreen)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = testingNavEntryProvider(),
    )
}

private fun testingNavEntryProvider(): (NavKey) -> NavEntry<NavKey> {
    return entryProvider {
        entry<ComponentScreen> {
            TestingScreen()
        }
    }
}
