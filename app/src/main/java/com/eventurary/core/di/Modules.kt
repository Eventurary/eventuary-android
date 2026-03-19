package com.eventurary.core.di

import com.eventurary.events.viewmodels.EventListViewModel
import com.eventurary.ui.viewmodels.EventDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { (event: String) -> EventDetailsViewModel(event) }
    viewModel { EventListViewModel() }
}

val allModules = listOf(
    appModule
)
