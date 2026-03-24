package com.eventurary.auth.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eventurary.auth.data.AuthTokens
import com.eventurary.core.data.CryptoManagerImpl
import com.eventurary.core.mocks.createTestDataStore
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertNull

// TODO: Fix this to check encryption
class TokensRepositoryImplTest {
    
    companion object {
        private val tokens = AuthTokens(
            accessToken = "access",
            refreshToken = "refresh",
            creationTime = 1000L,
            lifeSpan = 1000L,
        )
    }

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var cut: TokensRepositoryImpl
    private val mockCryptoManager = mockk<CryptoManagerImpl>()

    @Before
    fun setup() {
        dataStore = createTestDataStore()
        cut = TokensRepositoryImpl(
            dataStore = dataStore,
            cryptoManager = mockCryptoManager,
        )
    }

    @Test
    fun `authTokensFlow emits tokens after save`() = runTest {
        // WHEN
        cut.saveTokens(tokens)
        val emitted = cut.authTokensFlow.first()

        // THEN
        assertEquals(tokens, emitted)
    }

    @Test
    fun `authTokensFlow does not emit duplicates`() = runTest {
        // WHEN
        cut.saveTokens(tokens)
        cut.saveTokens(tokens)
        val emissions = cut.authTokensFlow.take(2).toList()

        // THEN
        assertEquals(1, emissions.distinct().size)
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
            it[key] = "invalid json"
        }

        // WHEN
        val result = cut.getTokens()

        // THEN
        assertNull(result)
    }
}
