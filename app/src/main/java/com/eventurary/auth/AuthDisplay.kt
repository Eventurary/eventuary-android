package com.eventurary.auth

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
import com.eventurary.auth.ui.screens.LoginScreen
import com.eventurary.auth.ui.screens.RegisterScreen
import com.eventurary.core.ui.navigation.Route

@Composable
fun AuthDisplay(
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(Route.AuthGraph.Login)
    val entryProvider = authNavEntryProvider(
        backStack = backStack,
        onSuccess = onSuccess,
    )

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = entryProvider,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
    )
}

private fun authNavEntryProvider(
    backStack: NavBackStack<NavKey>,
    onSuccess: () -> Unit,
): (NavKey) -> NavEntry<NavKey> = entryProvider {

    entry<Route.AuthGraph.Login> {
        LoginScreen(
            onSuccess = onSuccess,
            onRegisterRequested = { backStack.add(Route.AuthGraph.Register) },
        )
    }

    entry<Route.AuthGraph.Register> {
        RegisterScreen(onSuccess = onSuccess)
    }
}
