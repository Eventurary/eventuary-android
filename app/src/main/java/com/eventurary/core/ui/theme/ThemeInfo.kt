package com.eventurary.core.ui.theme

data class ThemeInfo(
    val isDarkTheme: Boolean,
) {
    val isLightTheme
        get() = !isDarkTheme
}
