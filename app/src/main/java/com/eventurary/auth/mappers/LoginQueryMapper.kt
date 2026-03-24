package com.eventurary.auth.mappers

import com.eventurary.auth.data.LoginQueryParams

class LoginQueryMapper : QueryParamMapper<LoginQueryParams> {

    companion object {
        const val EMAIL_QUERY = "email"
        const val PASSWORD_QUERY = "password"
    }

    override fun toQueryParams(params: LoginQueryParams) = mapOf(
        EMAIL_QUERY to params.email,
        PASSWORD_QUERY to params.password,
    )
}
