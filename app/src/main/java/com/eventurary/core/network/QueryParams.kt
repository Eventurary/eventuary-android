package com.eventurary.core.network

import io.ktor.http.URLBuilder

interface QueryParams {
    fun applyParams(urlBuilder: URLBuilder)
}
