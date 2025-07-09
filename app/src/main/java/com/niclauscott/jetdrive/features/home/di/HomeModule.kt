package com.niclauscott.jetdrive.features.home.di

import com.niclauscott.jetdrive.features.home.data.repository.HomeRepositoryImpl
import com.niclauscott.jetdrive.features.home.domain.repository.HomeRepository
import com.niclauscott.jetdrive.features.home.ui.HomeScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    factory<HomeRepository> { HomeRepositoryImpl(get(), get(), get()) }
    viewModel { HomeScreenViewModel(get()) }
}