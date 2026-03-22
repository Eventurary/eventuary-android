package com.eventurary.testing.ui.components

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.eventurary.testing.ui.components.ComponentRoutes.DCKey
import com.eventurary.testing.ui.components.ComponentRoutes.MarvelKey

@Composable
fun ComponentDisplay(
    modifier: Modifier,
    componentBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        entryProvider = componentNavEntryProvider(),
        backStack = componentBackStack,
        modifier = modifier,
        transitionSpec = {
            slideInHorizontally( initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it + 100 })
        }
    )
}

@Composable
private fun componentNavEntryProvider(): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<DCKey> {
        DC(it.hero)
    }

    entry<MarvelKey> {
        Marvel(it.hero)
    }
}
