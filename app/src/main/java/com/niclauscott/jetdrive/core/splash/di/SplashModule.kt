package com.niclauscott.jetdrive.core.splash.di

import com.niclauscott.jetdrive.core.splash.data.repository.AuthValidationRepositoryImpl
import com.niclauscott.jetdrive.core.splash.domain.repository.AuthValidationRepository
import com.niclauscott.jetdrive.core.splash.domain.SplashScreenViewModel
import org.koin.dsl.module

val splashModule = module {
    factory<AuthValidationRepository> { AuthValidationRepositoryImpl(get(), get(), get()) }
    factory { SplashScreenViewModel(get()) }
}

