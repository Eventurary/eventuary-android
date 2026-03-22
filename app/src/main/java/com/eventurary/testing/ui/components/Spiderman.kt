package com.eventurary.testing.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Composable
fun Spiderman() {
    Text(
        text = "Spiderman"
    )
}

@Serializable
data object SpidermanKey: NavKey
