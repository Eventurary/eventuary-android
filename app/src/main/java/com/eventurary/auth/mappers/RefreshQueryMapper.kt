package com.eventurary.auth.mappers

import com.eventurary.auth.data.RefreshQueryParams

class RefreshQueryMapper : QueryParamMapper<RefreshQueryParams> {

    companion object {
        const val REFRESH_TOKEN_QUERY = "refresh"
    }

    override fun toQueryParams(params: RefreshQueryParams) = mapOf(
        REFRESH_TOKEN_QUERY to params.refreshToken,
    )
}
