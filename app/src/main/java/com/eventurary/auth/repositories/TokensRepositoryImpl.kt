package com.eventurary.auth.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eventurary.auth.data.AuthTokens
import com.eventurary.core.data.CryptoException
import com.eventurary.core.data.CryptoManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class TokensRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager,
    private val json: Json = Json { encodeDefaults = true; ignoreUnknownKeys = true },
) : TokensRepository {

    companion object {
        private const val TOKENS_KEY = "auth_tokens"
        private val tokensPrefKey = stringPreferencesKey(TOKENS_KEY)
    }

    override val authTokensFlow: Flow<AuthTokens?> = dataStore.data
        .map { prefs ->
            val serialized = prefs[tokensPrefKey]
            decodeTokens(serialized)
        }
        .distinctUntilChanged()

    override suspend fun saveTokens(tokens: AuthTokens) {
        runCatching {
            val serialized = json.encodeToString(tokens)
            val encrypted = cryptoManager.encrypt(serialized)

            dataStore.edit { prefs ->
                prefs[tokensPrefKey] = encrypted
            }
        }.getOrElse { e ->
            when (e) {
                is CryptoException -> Napier.e(e) { "CryptoManager error when encrypting AuthTokens" }
                else -> Napier.e(e) { "Unknown exception when encoding AuthTokens" }
            }
        }
    }

    override suspend fun getTokens(): AuthTokens? {
        val prefs = dataStore.data.first()
        val serialized = prefs[tokensPrefKey]
        return decodeTokens(serialized)
    }

    override suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(tokensPrefKey)
        }
    }

    private fun decodeTokens(serialized: String?): AuthTokens? {
        if (serialized == null) return null

        return runCatching {
            val decrypted = cryptoManager.decrypt(serialized)
            json.decodeFromString<AuthTokens>(decrypted)
        }.getOrElse { e ->
            when (e) {
                is SerializationException -> Napier.e(e) { "Cannot deserialize tokens from string" }
                is IllegalArgumentException -> Napier.e(e) { "Cannot decode JSON to AuthTokens" }
                is CryptoException -> Napier.e(e) { "CryptoManager error when decrypting AuthTokens" }
                else -> Napier.e(e) { "Unknown exception when decoding AuthTokens" }
            }
            null
        }
    }
}
