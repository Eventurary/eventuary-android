package com.eventurary.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eventurary.core.ui.theme.EventuaryTheme

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    onRegisterRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = EventuaryTheme.materialColorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onSuccess) {
                Text("Login with details")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRegisterRequested) {
                Text("Try Register instead")
            }
        }
    }
}
