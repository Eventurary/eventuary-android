package com.eventurary.events.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eventurary.core.ui.theme.EventuaryTheme

@Composable
fun EventDetailsScreen(
    event: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = EventuaryTheme.materialColorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(event)
    }
}
