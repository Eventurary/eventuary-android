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
import com.eventurary.testing.ui.components.BatmanKey
import com.eventurary.testing.ui.components.ComponentDisplay
import com.eventurary.testing.ui.components.SpidermanKey

@Composable
fun TestingScreen() {
    val componentBackStack = rememberNavBackStack(BatmanKey)

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
                    replaceTopBackStack(SpidermanKey, componentBackStack)
                }
            ) {
                Text(
                    text = "Spiderman"
                )
            }

            Button(
                onClick = {
                    replaceTopBackStack(BatmanKey, componentBackStack)
                }
            ) {
                Text(
                    text = "Batman"
                )
            }

        }
    }
}

private fun replaceTopBackStack(component: NavKey, backStack: NavBackStack<NavKey>) {
    backStack.clear()
    backStack.add(component)
}
