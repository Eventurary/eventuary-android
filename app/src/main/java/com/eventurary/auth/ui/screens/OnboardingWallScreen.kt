package com.eventurary.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eventurary.core.ui.theme.EventuaryTheme

@Composable
fun OnboardingWallScreen(
    onContinueClicked: () -> Unit,
    onTestingClicked: () -> Unit,
    modifier: Modifier = Modifier,
    ) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = EventuaryTheme.materialColorScheme.primaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onContinueClicked,
            modifier = modifier.padding(bottom = 10.dp)
        ) {
            Text("Continue")
        }
        Button(
            onClick = onTestingClicked,
        ) {
            Text("Testing")
        }
    }
}
