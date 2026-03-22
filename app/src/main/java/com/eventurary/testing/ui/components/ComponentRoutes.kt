package com.eventurary.testing.ui.components

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface ComponentRoutes: NavKey {
    @Serializable
    data class MarvelKey(
        val hero: String
    ): ComponentRoutes

    @Serializable
    data class DCKey(
        val hero: String
    ): ComponentRoutes
}
