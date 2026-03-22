package com.eventurary.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val defaultDarkColorScheme = darkColorScheme(
    primary = Color(color = 0xFFD0BCFF),
    secondary = Color(color = 0xFFCCC2DC),
    tertiary = Color(color = 0xFFEFB8C8),
)

private val defaultLightColorScheme = lightColorScheme(
    primary = Color(color = 0xFF6650a4),
    secondary = Color(color = 0xFF625b71),
    tertiary = Color(color = 0xFF7D5260),
)

object EventuaryMaterialColors {
    var light: ColorScheme = defaultLightColorScheme
    var dark: ColorScheme = defaultDarkColorScheme
}
