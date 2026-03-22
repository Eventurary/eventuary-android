package com.eventurary.testing.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Marvel(heroName: String) {
    Text(
        text = "Marvel: $heroName"
    )
}
