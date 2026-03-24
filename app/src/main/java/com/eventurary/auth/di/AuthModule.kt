package com.eventurary.auth.di

import com.eventurary.auth.api.AuthApi
import com.eventurary.auth.api.AuthApiImpl
import com.eventurary.auth.mappers.AuthTokensMapper
import com.eventurary.auth.mappers.LoginQueryMapper
import com.eventurary.auth.mappers.RefreshQueryMapper
import com.eventurary.auth.mappers.RegisterQueryMapper
import com.eventurary.auth.repositories.TokensRepository
import com.eventurary.auth.repositories.TokensRepositoryImpl
import com.eventurary.auth.services.AuthService
import com.eventurary.auth.services.AuthServiceImpl
import com.eventurary.auth.useCases.GetAuthTokensUseCase
import com.eventurary.auth.useCases.GetAuthTokensUseCaseImpl
import com.eventurary.auth.useCases.HasAuthTokenAsFlowUseCase
import com.eventurary.auth.useCases.HasAuthTokenAsFlowUseCaseImpl
import com.eventurary.auth.useCases.IsLoggedInUseCase
import com.eventurary.auth.useCases.IsLoggedInUseCaseImpl
import com.eventurary.auth.useCases.IsTokensExpiredUseCase
import com.eventurary.auth.useCases.IsTokensExpiredUseCaseImpl
import com.eventurary.auth.useCases.LogoutUseCase
import com.eventurary.auth.useCases.LogoutUseCaseImpl
import com.eventurary.auth.useCases.RefreshTokensUseCase
import com.eventurary.auth.useCases.RefreshTokensUseCaseImpl
import com.eventurary.auth.viewmodels.AppViewModel
import com.eventurary.auth.viewmodels.AuthViewModel
import com.eventurary.core.di.HttpClientDIQualifiers
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    single<TokensRepository> {
        TokensRepositoryImpl(
            dataStore = get(),
            cryptoManager = get(),
        )
    }
    
    single<AuthApi> {
        AuthApiImpl(
            bffClient = get(named(HttpClientDIQualifiers.BFF)),
            loginQueryMapper = get(),
            registerQueryMapper = get(),
            refreshQueryMapper = get(),
        )
    }

    factory<AuthTokensMapper> {
        AuthTokensMapper(dateTimeProvider = get())
    }

    single<AuthService> {
        AuthServiceImpl(
            authApi = get(),
            tokensRepository = get(),
            authTokensMapper = get(),
        )
    }

    single<TokensRepository> {
        TokensRepositoryImpl(
            dataStore = get(),
            cryptoManager = get(),
        )
    }
    
    factory<HasAuthTokenAsFlowUseCase> {
        HasAuthTokenAsFlowUseCaseImpl(tokensRepository = get())
    }

    factory<IsLoggedInUseCase> {
        IsLoggedInUseCaseImpl(getAuthTokensUseCase = get())
    }

    factory<IsTokensExpiredUseCase> {
        IsTokensExpiredUseCaseImpl(dateTimeProvider = get())
    }

    factory<LogoutUseCase> {
        LogoutUseCaseImpl(authService = get())
    }

    factory<RefreshTokensUseCase> {
        RefreshTokensUseCaseImpl(
            authService = get(),
            isTokensExpiredUseCase = get(),
            connectivityProvider = get(),
            tokensRepository = get()
        )
    }

    factory<GetAuthTokensUseCase> {
        GetAuthTokensUseCaseImpl(
            tokensRepository = get(),
            isTokensExpiredUseCase = get(),
            refreshTokensUseCase = get()
        )
    }

    factory { LoginQueryMapper() }
    factory { RegisterQueryMapper() }
    factory { RefreshQueryMapper() }

    viewModel { AuthViewModel(authService = get()) }

    viewModel { AppViewModel(hasAuthTokenAsFlowUseCase = get()) }
}
