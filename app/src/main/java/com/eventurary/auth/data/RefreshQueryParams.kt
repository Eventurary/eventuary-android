package com.eventurary.auth.data

import com.eventurary.core.network.QueryParams
import io.ktor.http.URLBuilder
import kotlinx.serialization.Serializable

@Serializable
data class RefreshQueryParams(
    val refreshToken: String,
) : QueryParams {

    companion object {
        const val REFRESH_TOKEN_KEY = "refresh"
    }

    override fun applyParams(urlBuilder: URLBuilder) {
        urlBuilder.parameters.apply {
            this[REFRESH_TOKEN_KEY] = refreshToken
        }
    }
}
