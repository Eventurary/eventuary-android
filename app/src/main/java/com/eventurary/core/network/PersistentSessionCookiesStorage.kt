package com.eventurary.core.network

import com.eventurary.core.data.PreferencesDataStoreBridge
import com.eventurary.core.providers.DateTimeProvider
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.matches
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.collections.map

/*

TODO:
- List of cookies passed in to check for (domain, name)
- saveToDataStore -> only save passed in tokens
- function for removing login cookie?
- rename to PersistentSessionCookiesStorage

 */

class PersistentSessionCookiesStorage(
    private val preferencesDataStoreBridge: PreferencesDataStoreBridge,
    private val dateTimeProvider: DateTimeProvider,
) : CookiesStorage {

    companion object {
        const val COOKIES_KEY = "cookies"
    }

    private val cache = mutableListOf<Cookie>()
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
    private val mutex = Mutex()

    init {
        runBlocking {
            loadFromDataStore()
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) = mutex.withLock {
        if (cookie.name.isBlank()) return
        cache.removeAll { it.name == cookie.name && it.matches(requestUrl) }
        cache.add(cookie)
        saveToDataStore()
    }

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        cleanupExpired()
        return cache.filter { cookie ->
            cookie.matches(requestUrl)
        }
    }

    override fun close() {}

    private suspend fun loadFromDataStore() {
        val savedCookies = preferencesDataStoreBridge.getString(COOKIES_KEY)

        try {
            savedCookies?.let {
                cache.addAll(json.decodeFromString<List<Cookie>>(it))
            }
        } catch (e: Exception) {
            when (e) {
                is SerializationException -> Napier.e(e) { "Cannot deserialize cookies from string" }
                is IllegalArgumentException -> Napier.e(e) { "Cannot decode JSON to Cookie" }
                else -> throw e
            }
        }

        cleanupExpired()
    }

    private suspend fun saveToDataStore() {
        cleanupExpired()

        try {
            val serialized = json.encodeToString(cache.map { it })
            preferencesDataStoreBridge.setString(COOKIES_KEY, serialized)
        } catch (e: SerializationException) {
            Napier.e(e) { "Cannot serialize cookies to string" }
        }
    }

    // TODO: Only clean if oldest cookie is expired
    private fun cleanupExpired() {
        val now = dateTimeProvider.nowMillis
        cache.removeAll { it.isExpired(now) }
    }

    fun Cookie.isExpired(nowMillis: Long) =
        this.expires?.let { it.timestamp < nowMillis } ?: false
}
