package com.eventurary.auth.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eventurary.auth.data.AuthTokens
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class TokenRepositoryImpl(
    // TODO: Ensure this is encrypted, as right now will grab basic one
    private val encryptedDataStore: DataStore<Preferences>,
    private val json: Json = Json { encodeDefaults = true; ignoreUnknownKeys = true },
) : TokenRepository {

    companion object {
        private const val TOKENS_KEY = "auth_tokens"
        private val tokensPrefKey = stringPreferencesKey(TOKENS_KEY)
    }

    override val authTokensFlow: Flow<AuthTokens?> = encryptedDataStore.data
        .map { prefs ->
            val serialized = prefs[tokensPrefKey]
            decodeTokens(serialized)
        }
        .distinctUntilChanged()

    override suspend fun saveTokens(tokens: AuthTokens) {
        runCatching {
            val serialized = json.encodeToString(tokens)
            encryptedDataStore.edit { prefs ->
                prefs[tokensPrefKey] = serialized
            }
        }.getOrElse { e ->
            Napier.e(e) { "Cannot serialize cookies to string" }
        }
    }

    override suspend fun getTokens(): AuthTokens? {
        val prefs = encryptedDataStore.data.first()
        val serialized = prefs[tokensPrefKey]
        return decodeTokens(serialized)
    }

    override suspend fun clearTokens() {
        encryptedDataStore.edit { prefs ->
            prefs.remove(tokensPrefKey)
        }
    }

    private fun decodeTokens(serialized: String?): AuthTokens? {
        if (serialized == null) return null

        return runCatching {
            json.decodeFromString<AuthTokens>(serialized)
        }.getOrElse { e ->
            when (e) {
                is SerializationException -> Napier.e(e) { "Cannot deserialize tokens from string" }
                is IllegalArgumentException -> Napier.e(e) { "Cannot decode JSON to AuthTokens" }
                else -> throw e
            }
            null
        }
    }
}
