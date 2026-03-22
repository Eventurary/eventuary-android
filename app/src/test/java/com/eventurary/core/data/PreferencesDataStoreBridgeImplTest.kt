package com.eventurary.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PreferencesDataStoreBridgeImplTest {

    private companion object {
        private const val TEST_KEY = "test_key"
        private const val TEST_STRING_VALUE = "test_value"
    }

    private val mockPreferences = mockk<Preferences>()
    private val mockFlow = MutableStateFlow(mockPreferences)
    private val mockDataStore = mockk<DataStore<Preferences>>()
    private val cut = PreferencesDataStoreBridgeImpl(mockDataStore)

    @Before
    fun setUp() {
        coEvery { mockDataStore.data } returns mockFlow
    }

    @Test
    fun `when remove key then remove it`() = runTest {
        // GIVEN
        val secondKey = "second key"

        mockFlow.value = mutablePreferencesOf(
            stringPreferencesKey(TEST_KEY) to TEST_STRING_VALUE,
            stringPreferencesKey(secondKey) to TEST_STRING_VALUE,
        )

        coEvery { mockDataStore.updateData(any<suspend (Preferences) -> Preferences>()) } coAnswers {
            val transform = firstArg<suspend (Preferences) -> Preferences>()
            val updatedPrefs = transform.invoke(mockFlow.value)
            mockFlow.value = updatedPrefs.toMutablePreferences()
            updatedPrefs
        }

        // WHEN
        cut.removeKey(TEST_KEY)

        // THEN
        assertNull(cut.getString(TEST_KEY))
        assertEquals(cut.getString(secondKey), TEST_STRING_VALUE)
    }

    @Test
    fun `given valid key when getString then return value`() = runTest {
        // GIVEN
        val prefs = mutablePreferencesOf(stringPreferencesKey(TEST_KEY) to TEST_STRING_VALUE)
        coEvery { mockDataStore.data } returns flowOf(prefs)

        // WHEN
        val result = cut.getString(TEST_KEY)

        // THEN
        assertEquals(TEST_STRING_VALUE, result)
        coVerify { mockDataStore.data }
    }

    @Test
    fun `given invalid key when getString then return null`() = runTest {
        // GIVEN
        val prefs = mutablePreferencesOf()
        coEvery { mockDataStore.data } returns flowOf(prefs)

        // WHEN
        val result = cut.getString(TEST_KEY)

        // THEN
        assertNull(result)
        coVerify { mockDataStore.data }
    }

    @Test
    fun `when setString then store value`() = runTest {
        // GIVEN
        mockFlow.value = mutablePreferencesOf()

        coEvery { mockDataStore.updateData(any<suspend (Preferences) -> Preferences>()) } coAnswers {
            val transform = firstArg<suspend (Preferences) -> Preferences>()
            val updatedPrefs = transform.invoke(mockFlow.value)
            mockFlow.value = updatedPrefs.toMutablePreferences()
            updatedPrefs
        }

        // WHEN
        cut.setString(TEST_KEY, TEST_STRING_VALUE)

        // THEN
        assertEquals(cut.getString(TEST_KEY), TEST_STRING_VALUE)
    }
}
