package com.eventurary.auth.data

import io.ktor.http.URLBuilder
import junit.framework.TestCase.assertEquals
import org.junit.Test

class RegisterQueryParamsTest {

    @Test
    fun `applyParams adds values to URLBuilder`() {
        // GIVEN
        val params = RegisterQueryParams(
            username = "username",
            email = "email",
            password = "password",
        )
        val builder = URLBuilder()

        // WHEN
        params.applyParams(builder)

        // THEN
        assertEquals("username", builder.parameters[RegisterQueryParams.USERNAME_KEY])
        assertEquals("email", builder.parameters[RegisterQueryParams.EMAIL_KEY])
        assertEquals("password", builder.parameters[RegisterQueryParams.PASSWORD_KEY])
    }

    @Test
    fun `applyParams replaces previous query params`() {
        // GIVEN
        val builder = URLBuilder().apply {
            parameters.append(RegisterQueryParams.USERNAME_KEY, "old")
        }
        val params = RegisterQueryParams(
            username = "new",
            email = "email",
            password = "password",
        )

        // WHEN
        params.applyParams(builder)

        // THEN
        val usernames = builder.parameters.getAll(RegisterQueryParams.USERNAME_KEY)
        assertEquals(listOf("new"), usernames)
    }
}
