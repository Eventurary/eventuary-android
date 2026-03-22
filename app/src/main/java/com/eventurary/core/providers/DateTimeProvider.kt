package com.eventurary.core.providers

import java.time.Instant
import java.time.ZonedDateTime

interface DateTimeProvider {
    val nowMillis: Long
    val nowInstant: Instant
    val nowZonedDateTime: ZonedDateTime
}

class DateTimeProviderImpl : DateTimeProvider {
    override val nowMillis: Long get() = System.currentTimeMillis()
    override val nowInstant: Instant get() = Instant.now()
    override val nowZonedDateTime: ZonedDateTime get() = ZonedDateTime.now()
}
