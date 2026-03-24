package com.eventurary.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.eventurary.auth.di.authModule
import com.eventurary.core.data.CryptoManager
import com.eventurary.core.data.CryptoManagerImpl
import com.eventurary.core.data.PreferencesDataStoreBridge
import com.eventurary.core.data.PreferencesDataStoreBridgeImpl
import com.eventurary.core.providers.ConnectivityProvider
import com.eventurary.core.providers.ConnectivityProviderImpl
import com.eventurary.core.providers.DateTimeProvider
import com.eventurary.core.providers.DateTimeProviderImpl
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val EVENTUARY_DATA_STORE = "eventuaryDataStore"

val coreModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile(EVENTUARY_DATA_STORE)
        }
    }

    single<PreferencesDataStoreBridge> {
        PreferencesDataStoreBridgeImpl(dataStore = get())
    }

    single<DateTimeProvider> {
        DateTimeProviderImpl()
    }

    single<ConnectivityProvider> {
        ConnectivityProviderImpl(context = androidContext())
    }

    single<CryptoManager> {
        CryptoManagerImpl()
    }

    single<HttpClient>(named(HttpClientDIQualifiers.BFF)) {
        provideBffHttpClient(
            getAuthTokensUseCase = get(),
            refreshTokensUseCase = get()
        )
    }
}

val allModules = listOf(
    coreModule,
    authModule,
)
