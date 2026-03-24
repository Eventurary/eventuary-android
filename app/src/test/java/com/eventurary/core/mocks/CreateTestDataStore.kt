package com.eventurary.core.mocks

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File

fun createTestDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        scope = CoroutineScope(Dispatchers.Unconfined + Job()),
        produceFile = { File.createTempFile("test", ".preferences_pb") }
    )
}
