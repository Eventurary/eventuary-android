package com.eventurary.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class RefreshQueryParams(
    val refreshToken: String,
)
