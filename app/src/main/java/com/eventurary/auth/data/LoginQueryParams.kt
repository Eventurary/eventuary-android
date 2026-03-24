package com.eventurary.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginQueryParams(
    val email: String,
    val password: String,
)
