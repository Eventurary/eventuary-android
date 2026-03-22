package com.eventurary.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokensDTO(
    val accessToken: String,
    val refreshToken: String,
    val lifeSpan: Long,
)
