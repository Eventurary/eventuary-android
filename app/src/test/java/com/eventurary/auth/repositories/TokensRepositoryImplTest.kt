package com.eventurary.auth.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eventurary.auth.data.AuthTokens
import com.eventurary.core.data.CryptoManagerImpl
import com.eventurary.core.mocks.createTestDataStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertNull

class TokensRepositoryImplTest {
    
    companion object {
        private val tokens = AuthTokens(
            accessToken = "access",
            refreshToken = "refresh",
            creationTime = 1000L,
            lifeSpan = 1000L,
        )
    }

    private val mockCryptoManager = mockk<CryptoManagerImpl>()
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var cut: TokensRepositoryImpl

    @Before
    fun setup() {
        every { mockCryptoManager.encrypt(any()) } answers {
            "enc:${firstArg<String>()}"
        }

        every { mockCryptoManager.decrypt(any()) } answers {
            firstArg<String>().removePrefix("enc:")
        }

        dataStore = createTestDataStore()
        cut = TokensRepositoryImpl(
            dataStore = dataStore,
            cryptoManager = mockCryptoManager,
        )
    }

    @Test
    fun `saveTokens encrypts before storing`() = runTest {
        // WHEN
        cut.saveTokens(tokens)

        // THEN
        verify(exactly = 1) {
            mockCryptoManager.encrypt(match { it.contains("access") })
        }
    }

    @Test
    fun `getTokens decrypts stored value`() = runTest {
        // GIVEN
        cut.saveTokens(tokens)

        // WHEN
        cut.getTokens()

        // THEN
        verify(atLeast = 1) { mockCryptoManager.decrypt(any()) }
    }

    @Test
    fun `authTokensFlow emits tokens after save`() = runTest {
        // WHEN
        cut.saveTokens(tokens)
        val emitted = cut.authTokensFlow.first()

        // THEN
        assertEquals(tokens, emitted)
        verify { mockCryptoManager.decrypt(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `authTokensFlow does not emit duplicates`() = runTest {
        // GIVEN
        val emissions = mutableListOf<AuthTokens?>()

        // WHEN
        val job = launch {
            cut.authTokensFlow.collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        cut.saveTokens(tokens)
        advanceUntilIdle()
        cut.saveTokens(tokens)
        advanceUntilIdle()

        job.cancel()

        // THEN
        assertEquals(listOf(null, tokens), emissions)
    }

    @Test
    fun `saveTokens then getTokens returns same value`() = runTest {
        // WHEN
        cut.saveTokens(tokens)
        val result = cut.getTokens()

        // THEN
        assertEquals(tokens, result)
    }

    @Test
    fun `clearTokens removes stored tokens`() = runTest {
        // WHEN
        cut.saveTokens(tokens)
        cut.clearTokens()
        val result = cut.getTokens()

        // THEN
        assertNull(result)
    }

    @Test
    fun `invalid JSON returns null`() = runTest {
        // GIVEN
        val key = stringPreferencesKey("auth_tokens")
        dataStore.edit {
            it[key] = "enc:invalid json"
        }

        // WHEN
        val result = cut.getTokens()

        // THEN
        assertNull(result)
        verify { mockCryptoManager.decrypt("enc:invalid json") }
    }

    @Test
    fun `saveTokens does not store value when encrypt throws`() = runTest {
        // GIVEN
        val key = stringPreferencesKey("auth_tokens")
        every { mockCryptoManager.encrypt(any()) } throws RuntimeException("Crypto failure")

        // WHEN
        cut.saveTokens(tokens)

        // THEN
        val prefs = dataStore.data.first()
        assertNull(prefs[key])
        verify { mockCryptoManager.encrypt(any()) }
    }

    @Test
    fun `getTokens returns null when decrypt throws`() = runTest {
        // GIVEN
        every { mockCryptoManager.decrypt(any()) } throws RuntimeException("Crypto failure")

        // WHEN
        cut.saveTokens(tokens)
        val result = cut.getTokens()

        // THEN
        assertNull(result)
        verify { mockCryptoManager.decrypt(any()) }
    }
}
