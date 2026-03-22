package com.eventurary.core.data

interface PreferencesDataStoreBridge {
    suspend fun removeKey(key: String)
    suspend fun getString(key: String): String?
    suspend fun setString(key: String, value: String)
}

