package com.eventurary.core.network

import com.eventurary.core.data.PreferencesDataStoreBridge
import com.eventurary.core.network.PersistentSessionCookiesStorage.Companion.COOKIES_KEY
import com.eventurary.core.providers.DateTimeProvider
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PersistentSessionCookiesStorageTest {

    private companion object {
        private const val BASE_TIME = 1000000000000L
        private const val SERIALIZED_COOKIES = "[{" +
            "\"name\":\"name1\", " +
            "\"domain\":\"example.com\", " +
            "\"value\":\"value1\"" +
            "\"path\":\"/\"" +
        "}]"
        private val testUrl = Url("http://example.com")

        private val validCookie = Cookie(
            name = "name1",
            domain = "example.com",
            value = "value1",
            path = "/",
            expires = GMTDate(BASE_TIME + 3600000),
        )

        private val expiredCookie = Cookie(
            name = "name2",
            value = "value2",
            domain = "example.com",
            path = "/",
            expires = GMTDate(BASE_TIME - 3600000),
        )

        private val differentDomainCookie = Cookie(
            name = "name3",
            value = "value3",
            domain = "different.com",
            path = "/",
            expires = GMTDate(BASE_TIME + 3600000),
        )
    }

    private val mockDataStore = mockk<PreferencesDataStoreBridge>()
    private val mockDateTimeProvider = mockk<DateTimeProvider>()
    private lateinit var cut: PersistentSessionCookiesStorage

    @Before
    fun setUp() {
        coEvery { mockDateTimeProvider.nowMillis } returns BASE_TIME
        coEvery { mockDataStore.getString(COOKIES_KEY) } returns null
        coEvery { mockDataStore.setString(any(), any()) } just Runs

        cut = createPersistentCookieStorage()
    }

    @Test
    fun `cookies should load from data store on initialization`() = runTest {
        // GIVEN
        coEvery { mockDataStore.getString(COOKIES_KEY) } returns SERIALIZED_COOKIES

        // WHEN
        cut = createPersistentCookieStorage()
        val cookies = cut.get(testUrl)

        // THEN
        coVerify { mockDataStore.getString(COOKIES_KEY) }
        assertEquals(1, cookies.size)
        assertEquals("name1", cookies[0].name)
    }

    @Test
    fun `invalid cookies should fail gracefully on load from initialization`() = runTest {
        // GIVEN
        coEvery { mockDataStore.getString(COOKIES_KEY) } returns "invalid json"

        // WHEN
        cut = createPersistentCookieStorage()
        val cookies = cut.get(testUrl)

        // THEN
        coVerify { mockDataStore.getString(COOKIES_KEY) }
        assertEquals(0, cookies.size)
    }

    @Test
    fun `addCookie should save it to prefs`() = runTest {
        // WHEN
        cut.addCookie(testUrl, validCookie)
        val cookies = cut.get(testUrl)

        // THEN
        coVerify(exactly = 1) { mockDataStore.setString(COOKIES_KEY, any()) }
        assertEquals(1, cookies.size)
        assertEquals("name1", cookies[0].name)
    }

    @Test
    fun `addCookie should replace duplicate cookies`() = runTest {
        // WHEN
        cut.addCookie(testUrl, validCookie)
        cut.addCookie(testUrl, validCookie)
        val cookies = cut.get(testUrl)

        // THEN
        coVerify(exactly = 2) { mockDataStore.setString(COOKIES_KEY, any()) }
        assertEquals(1, cookies.size)
        assertEquals("name1", cookies[0].name)
    }

    @Test
    fun `addCookie should do nothing with no name`() = runTest {
        // GIVEN
        val noNameCookie = validCookie.copy(name = "")

        // WHEN
        cut.addCookie(testUrl, noNameCookie)
        val cookies = cut.get(testUrl)

        // THEN
        assertEquals(0, cookies.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `successive addCookie should be mutually exclusive`() = runTest {
        // GIVEN
        val gate = CompletableDeferred<Unit>()
        coEvery { mockDataStore.setString(any(), any()) } coAnswers {
            gate.await()
        }

        // WHEN
        launch {
            cut.addCookie(testUrl, validCookie)
            cut.addCookie(testUrl, validCookie)
        }
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 1) { mockDataStore.setString(any(), any()) }
        gate.complete(Unit)
        advanceUntilIdle()
        coVerify(exactly = 2) { mockDataStore.setString(any(), any()) }
    }

    @Test
    fun `get should filter cookies by domain and expire time`() = runTest {
        // GIVEN
        cut.addCookie(testUrl, validCookie)
        cut.addCookie(testUrl, expiredCookie)
        cut.addCookie(testUrl, differentDomainCookie)

        // WHEN
        val cookies = cut.get(testUrl)

        // THEN
        assertEquals(1, cookies.size)
        assertEquals(validCookie, cookies[0])
        verify(exactly = 5) { mockDateTimeProvider.nowMillis }
    }

    private fun createPersistentCookieStorage() =
        PersistentSessionCookiesStorage(
            preferencesDataStoreBridge = mockDataStore,
            dateTimeProvider = mockDateTimeProvider,
        )
}