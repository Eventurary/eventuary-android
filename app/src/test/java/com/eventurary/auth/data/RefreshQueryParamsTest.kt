package com.eventurary.auth.data

import io.ktor.http.URLBuilder
import junit.framework.TestCase.assertEquals
import org.junit.Test

class RefreshQueryParamsTest {

    @Test
    fun `applyParams adds values to URLBuilder`() {
        // GIVEN
        val params = RefreshQueryParams(
            refreshToken = "refresh"
        )
        val builder = URLBuilder()

        // WHEN
        params.applyParams(builder)

        // THEN
        assertEquals("refresh", builder.parameters[RefreshQueryParams.REFRESH_TOKEN_KEY])
    }

    @Test
    fun `applyParams replaces previous query params`() {
        // GIVEN
        val builder = URLBuilder().apply {
            parameters.append(RefreshQueryParams.REFRESH_TOKEN_KEY, "stale")
        }
        val params = RefreshQueryParams(
            refreshToken = "new"
        )

        // WHEN
        params.applyParams(builder)

        // THEN
        val refreshTokens = builder.parameters.getAll(RefreshQueryParams.REFRESH_TOKEN_KEY)
        assertEquals(listOf("new"), refreshTokens)
    }
}
