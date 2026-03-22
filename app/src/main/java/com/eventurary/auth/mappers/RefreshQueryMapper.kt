package com.eventurary.auth.mappers

import com.eventurary.auth.data.RefreshRequest

class RefreshQueryMapper : QueryParamMapper<RefreshRequest> {

    companion object {
        const val REFRESH_TOKEN_QUERY = "refresh"
    }

    override fun toQueryParams(obj: RefreshRequest) = mapOf(
        REFRESH_TOKEN_QUERY to obj.refreshToken,
    )
}
