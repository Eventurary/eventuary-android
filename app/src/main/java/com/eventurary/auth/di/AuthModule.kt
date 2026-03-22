package com.eventurary.auth.di

import com.eventurary.auth.api.AuthApi
import com.eventurary.auth.api.AuthApiImpl
import com.eventurary.auth.mappers.AuthTokensMapper
import com.eventurary.auth.mappers.LoginQueryMapper
import com.eventurary.auth.mappers.RefreshQueryMapper
import com.eventurary.auth.mappers.RegisterQueryMapper
import com.eventurary.auth.repositories.TokenRepository
import com.eventurary.auth.repositories.TokenRepositoryImpl
import com.eventurary.auth.services.AuthService
import com.eventurary.auth.services.AuthServiceImpl
import com.eventurary.auth.useCases.HasAuthTokenAsFlowUseCase
import com.eventurary.auth.useCases.HasAuthTokenAsFlowUseCaseImpl
import com.eventurary.auth.useCases.IsLoggedInUseCase
import com.eventurary.auth.useCases.IsLoggedInUseCaseImpl
import com.eventurary.auth.useCases.IsTokensExpiredUseCase
import com.eventurary.auth.useCases.IsTokensExpiredUseCaseImpl
import com.eventurary.auth.useCases.LogoutUseCase
import com.eventurary.auth.useCases.LogoutUseCaseImpl
import com.eventurary.auth.useCases.RefreshTokenUseCase
import com.eventurary.auth.useCases.RefreshTokenUseCaseImpl
import com.eventurary.auth.viewmodels.AppViewModel
import com.eventurary.auth.viewmodels.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<TokenRepository> {
        TokenRepositoryImpl(encryptedDataStore = get())
    }
    
    single<AuthApi> {
        AuthApiImpl(client = get())
    }

    factory<AuthTokensMapper> {
        AuthTokensMapper(get())
    }

    single<AuthService> {
        AuthServiceImpl(
            authApi = get(),
            tokenRepository = get(),
            authTokensMapper = get(),
        )
    }

    single<TokenRepository> {
        TokenRepositoryImpl(
            encryptedDataStore = get(), // TODO: Supply encrypted version, not non-encrypted version
        )
    }
    
    factory<HasAuthTokenAsFlowUseCase> {
        HasAuthTokenAsFlowUseCaseImpl(get())
    }

    factory<IsLoggedInUseCase> {
        IsLoggedInUseCaseImpl(get(), get(), get())
    }

    factory<IsTokensExpiredUseCase> {
        IsTokensExpiredUseCaseImpl(get())
    }

    factory<LogoutUseCase> {
        LogoutUseCaseImpl(get())
    }

    factory<RefreshTokenUseCase> {
        RefreshTokenUseCaseImpl(get(), get(), get())
    }

    factory { LoginQueryMapper() }
    factory { RegisterQueryMapper() }
    factory { RefreshQueryMapper() }

    viewModel { AuthViewModel(get()) }

    viewModel { AppViewModel(get()) }
}
