package com.eventurary.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.eventurary.BuildConfig
import com.eventurary.auth.di.authModule
import com.eventurary.core.data.PreferencesDataStoreBridge
import com.eventurary.core.data.PreferencesDataStoreBridgeImpl
import com.eventurary.core.providers.ConnectivityProvider
import com.eventurary.core.providers.ConnectivityProviderImpl
import com.eventurary.core.providers.DateTimeProvider
import com.eventurary.core.providers.DateTimeProviderImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val EVENTUARY_DATA_STORE = "eventuaryDataStore"
private const val HTTP_TIMEOUT = 10000

val coreModule = module {
    // TODO: Generic and secure data store - no encryption currently
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

    single<ConnectivityProvider> {
        ConnectivityProviderImpl(androidContext())
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
                requestTimeoutMillis = HTTP_TIMEOUT
            }
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(DefaultRequest) {
                url(BuildConfig.BASE_URL)
            }
        }
    }
}

val allModules = listOf(
    coreModule,
    authModule,
)
