package com.eventurary.auth.mappers

interface QueryParamMapper<T> {
    fun toQueryParams(params: T): Map<String, String>
}
