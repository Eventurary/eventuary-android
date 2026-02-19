package com.eventurary.ui.theme

data class ThemeInfo(
    val isDarkTheme: Boolean,
) {
    val isLightTheme
        get() = !isDarkTheme
}
