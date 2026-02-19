package com.eventurary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalCustomColors =
    staticCompositionLocalOf {
        EventuaryCustomColors.light
    }

private val LocalThemeInfo =
    staticCompositionLocalOf {
        ThemeInfo(isDarkTheme = false)
    }

@Composable
fun EventuraryTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val materialColorScheme = if (isDarkTheme) EventuaryMaterialColors.dark else EventuaryMaterialColors.light
    val customColorScheme = if (isDarkTheme) EventuaryCustomColors.dark else EventuaryCustomColors.light
    val themeInfo = ThemeInfo(isDarkTheme)

    CompositionLocalProvider(
        LocalCustomColors provides customColorScheme,
        LocalThemeInfo provides themeInfo,
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = defaultTypography,
            content = content
        )
    }
}

object EventuaryTheme {
    val materialColorScheme: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme
    val customColorScheme: CustomColorScheme
        @Composable
        get() = LocalCustomColors.current
    val typography: Typography
        @Composable
        get() = MaterialTheme.typography
    val info: ThemeInfo
        @Composable
        get() = LocalThemeInfo.current
}