package com.eventurary.ui.theme.utils

import androidx.compose.runtime.Composable
import com.eventurary.ui.theme.EventuaryTheme

@Composable
fun <T> onDarkMode(
    darkMode: T,
    lightMode: T
): T {
    return if (EventuaryTheme.info.isDarkTheme) {
        darkMode
    } else {
        lightMode
    }
}