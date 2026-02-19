package com.eventurary.ui.theme

import androidx.compose.ui.graphics.Color

data class CustomColorScheme(
    // Custom color example - remove on first use
    val customColor: Color,
)

val defaultLightCustomColorScheme = CustomColorScheme(
    // Custom color example - remove on first use
    customColor = Color(color = 0xFFFFFFFF),
)

val defaultDarkCustomColorScheme = CustomColorScheme(
    // Custom color example - remove on first use
    customColor = Color(color = 0x000000FF),
)

object EventuaryCustomColors {
    var light: CustomColorScheme = defaultLightCustomColorScheme
    var dark: CustomColorScheme = defaultDarkCustomColorScheme
}