package com.eventurary.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class PreferencesDataStoreBridgeImpl(
    private val dataStore: DataStore<Preferences>
) : PreferencesDataStoreBridge {

    override suspend fun getString(key: String): String? {
        val prefs = dataStore.data.first()
        return prefs[stringPreferencesKey(key)]
    }

    override suspend fun setString(key: String, value: String) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }
}
