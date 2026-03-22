package com.eventurary.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val creationTime: Long,
    val lifeSpan: Long,
)
