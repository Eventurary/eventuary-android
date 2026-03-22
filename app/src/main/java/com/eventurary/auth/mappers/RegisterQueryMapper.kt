package com.eventurary.auth.mappers

import com.eventurary.auth.data.RegisterRequest

class RegisterQueryMapper : QueryParamMapper<RegisterRequest> {

    companion object {
        const val EMAIL_QUERY = "email"
        const val PASSWORD_QUERY = "password"
        const val NAME_QUERY = "name"
    }

    override fun toQueryParams(obj: RegisterRequest) = mapOf(
        EMAIL_QUERY to obj.email,
        PASSWORD_QUERY to obj.password,
        NAME_QUERY to obj.username,
    )
}
