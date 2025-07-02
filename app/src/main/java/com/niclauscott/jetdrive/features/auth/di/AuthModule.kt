package com.niclauscott.jetdrive.features.auth.di

import com.niclauscott.jetdrive.features.auth.data.repository.AuthRepositoryImpl
import com.niclauscott.jetdrive.features.auth.domain.repository.AuthRepository
import com.niclauscott.jetdrive.features.auth.ui.screen.login.LoginScreenVieModel
import com.niclauscott.jetdrive.features.auth.ui.screen.register.RegistrationScreenVieModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    factory<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    viewModel { param -> LoginScreenVieModel(get(), get(), param.get()) }
    viewModel { param -> RegistrationScreenVieModel(get(), param.get()) }
}