package com.eventurary.auth.mappers

import com.eventurary.auth.data.RegisterQueryParams

class RegisterQueryMapper : QueryParamMapper<RegisterQueryParams> {

    companion object {
        const val EMAIL_QUERY = "email"
        const val PASSWORD_QUERY = "password"
        const val NAME_QUERY = "name"
    }

    override fun toQueryParams(params: RegisterQueryParams) = mapOf(
        EMAIL_QUERY to params.email,
        PASSWORD_QUERY to params.password,
        NAME_QUERY to params.username,
    )
}
