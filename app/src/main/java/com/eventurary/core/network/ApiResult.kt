package com.eventurary.core.network

sealed class ApiResult {
    object Success : ApiResult()
    data class Error(val statusCode: Int, val message: String) : ApiResult()
}
