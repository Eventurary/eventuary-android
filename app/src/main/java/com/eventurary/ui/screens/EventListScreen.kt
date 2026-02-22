package com.eventurary.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eventurary.ui.viewmodels.EventListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventListScreen(
    modifier: Modifier = Modifier,
    onEventClick: (String) -> Unit
) {
    val viewModel: EventListViewModel = koinViewModel()

    val events by viewModel.events.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(events) { event ->
            Text(
                text = event,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEventClick(event)
                    }
                    .padding(16.dp)
            )
        }
    }
}