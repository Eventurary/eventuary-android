package com.eventurary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eventurary.ui.theme.EventuaryTheme
import com.eventurary.ui.viewmodels.EventDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EventDetailsScreen(
    event: String,
    modifier: Modifier = Modifier,
) {
    val viewModel: EventDetailsViewModel = koinViewModel(parameters = { parametersOf(event) })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = EventuaryTheme.materialColorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(event)
    }
}