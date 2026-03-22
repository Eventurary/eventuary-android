package com.eventurary.core.network

import com.eventurary.core.data.PreferencesDataStoreBridge

class SessionRepositoryImpl(
    private val preferencesDataStoreBridge: PreferencesDataStoreBridge
) : SessionRepository {

    companion object {
        const val SESSION_TOKEN_KEY = "session_token"
    }

    override suspend fun saveSessionToken(token: String) {
        preferencesDataStoreBridge.setString(SESSION_TOKEN_KEY, token)
    }

    override suspend fun getSessionToken(): String? {
        return preferencesDataStoreBridge.getString(SESSION_TOKEN_KEY)
    }

    override suspend fun clearSessionToken() {
        preferencesDataStoreBridge.setString(SESSION_TOKEN_KEY, "")
    }
}