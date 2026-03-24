package com.eventurary.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterQueryParams(
    val username: String,
    val email: String,
    val password: String,
)
