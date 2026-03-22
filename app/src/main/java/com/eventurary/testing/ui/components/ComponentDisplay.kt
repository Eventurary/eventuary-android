package com.eventurary.testing.ui.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.eventurary.testing.ui.components.ComponentRoutes.BatmanKey
import com.eventurary.testing.ui.components.ComponentRoutes.SpidermanKey

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

private fun componentNavEntryProvider(): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<BatmanKey> {
        Batman()
    }

    entry<SpidermanKey> {
        Spiderman()
    }
}
