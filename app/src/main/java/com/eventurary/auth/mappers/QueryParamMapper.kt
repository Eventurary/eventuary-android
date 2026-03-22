package com.eventurary.auth.mappers

interface QueryParamMapper<T> {
    fun toQueryParams(obj: T): Map<String, String>
}
