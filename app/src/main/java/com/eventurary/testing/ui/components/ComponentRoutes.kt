package com.eventurary.testing.ui.components

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface ComponentRoutes: NavKey {
    @Serializable
    data object SpidermanKey: ComponentRoutes

    @Serializable
    data object BatmanKey: ComponentRoutes
}
