package com.niclauscott.jetdrive.features.landing.di

import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val landingModule = module {
    viewModel { param -> LandingScreenViewModel(get(), param.get()) }
}