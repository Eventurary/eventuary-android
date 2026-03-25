package com.eventurary.core.data

class CryptoException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
