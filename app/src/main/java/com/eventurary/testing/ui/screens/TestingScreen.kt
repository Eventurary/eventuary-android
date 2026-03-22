package com.eventurary.testing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.eventurary.core.ui.theme.EventuaryTheme
import com.eventurary.testing.ui.components.ComponentDisplay
import com.eventurary.testing.ui.components.ComponentRoutes
import com.eventurary.testing.ui.components.ComponentRoutes.MarvelKey
import com.eventurary.testing.ui.components.ComponentRoutes.DCKey

@Composable
fun TestingScreen() {
    val componentBackStack = rememberNavBackStack(DCKey("Superman"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = EventuaryTheme.materialColorScheme.primaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ComponentDisplay(
            modifier = Modifier.padding(bottom = 10.dp),
            componentBackStack = componentBackStack
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    replaceTopBackStack(MarvelKey(hero = "Spiderman"), componentBackStack)
                }
            ) {
                Text(
                    text = "Spiderman"
                )
            }

            Button(
                onClick = {
                    replaceTopBackStack(DCKey(hero = "Batman"), componentBackStack)
                }
            ) {
                Text(
                    text = "Batman"
                )
            }

        }
    }
}

private fun replaceTopBackStack(component: ComponentRoutes, backStack: NavBackStack<NavKey>) {
    backStack.clear()
    backStack.add(component)
}
