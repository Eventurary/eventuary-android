package com.eventurary.auth.data

import com.eventurary.core.network.QueryParams
import io.ktor.http.URLBuilder
import kotlinx.serialization.Serializable

@Serializable
data class RegisterQueryParams(
    val username: String,
    val email: String,
    val password: String,
) : QueryParams {

    companion object {
        const val USERNAME_KEY = "username"
        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
    }

    override fun applyParams(urlBuilder: URLBuilder) {
        urlBuilder.parameters.apply {
            this[USERNAME_KEY] = username
            this[EMAIL_KEY] = email
            this[PASSWORD_KEY] = password
        }
    }
}
