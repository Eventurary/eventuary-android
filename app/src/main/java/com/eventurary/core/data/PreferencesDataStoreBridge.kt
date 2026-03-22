package com.eventurary.core.data

interface PreferencesDataStoreBridge {
    suspend fun getString(key: String): String?
    suspend fun setString(key: String, value: String)
}

