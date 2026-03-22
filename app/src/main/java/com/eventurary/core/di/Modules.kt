package com.eventurary.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.eventurary.BuildConfig
import com.eventurary.core.data.PreferencesDataStoreBridge
import com.eventurary.core.data.PreferencesDataStoreBridgeImpl
import com.eventurary.core.network.PersistentSessionCookiesStorage
import com.eventurary.core.providers.DateTimeProvider
import com.eventurary.core.providers.DateTimeProviderImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val EVENTUARY_DATA_STORE = "eventuaryDataStore"

val coreModule = module {
    // TODO: Generic and secure data store - no encryption
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile(EVENTUARY_DATA_STORE)
        }
    }

    single<PreferencesDataStoreBridge> {
        PreferencesDataStoreBridgeImpl(get())
    }

    single<DateTimeProvider> {
        DateTimeProviderImpl()
    }

    single<CookiesStorage> {
        PersistentSessionCookiesStorage(
            preferencesDataStoreBridge = get(),
            dateTimeProvider = get()
        )
    }

    single<HttpClient> {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.BODY
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
            }
            install(HttpCookies) {
                storage = get()
            }
            install(DefaultRequest) {
                url(BuildConfig.BASE_URL)
            }
        }
    }
}

val allModules = listOf(
    coreModule
)
