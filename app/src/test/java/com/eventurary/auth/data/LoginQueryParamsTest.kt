package com.eventurary.auth.data

import io.ktor.http.URLBuilder
import junit.framework.TestCase.assertEquals
import org.junit.Test

class LoginQueryParamsTest {

    @Test
    fun `applyParams adds values to URLBuilder`() {
        // GIVEN
        val params = LoginQueryParams(
            email = "test@example.com",
            password = "secret"
        )
        val builder = URLBuilder()

        // WHEN
        params.applyParams(builder)

        // THEN
        assertEquals("test@example.com", builder.parameters[LoginQueryParams.EMAIL_KEY])
        assertEquals("secret", builder.parameters[LoginQueryParams.PASSWORD_KEY])
    }

    @Test
    fun `applyParams replaces previous query params`() {
        // GIVEN
        val builder = URLBuilder().apply {
            parameters.append(LoginQueryParams.EMAIL_KEY, "old@example.com")
        }
        val params = LoginQueryParams(
            email = "new@example.com",
            password = "secret"
        )

        // WHEN
        params.applyParams(builder)

        // THEN
        val emails = builder.parameters.getAll(LoginQueryParams.EMAIL_KEY)
        assertEquals(listOf("new@example.com"), emails)
    }
}
