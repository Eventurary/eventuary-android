package com.eventurary.auth.mappers

import com.eventurary.auth.data.LoginRequest

class LoginQueryMapper : QueryParamMapper<LoginRequest> {

    companion object {
        const val EMAIL_QUERY = "email"
        const val PASSWORD_QUERY = "password"
    }

    override fun toQueryParams(obj: LoginRequest) = mapOf(
        EMAIL_QUERY to obj.email,
        PASSWORD_QUERY to obj.password,
    )
}
