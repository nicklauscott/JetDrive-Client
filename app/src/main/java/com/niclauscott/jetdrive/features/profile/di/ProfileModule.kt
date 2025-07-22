package com.niclauscott.jetdrive.features.profile.di

import com.niclauscott.jetdrive.features.profile.data.repository.ProfileRepositoryImpl
import com.niclauscott.jetdrive.features.profile.domain.repository.ProfileRepository
import com.niclauscott.jetdrive.features.profile.ui.ProfileScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get()) }
    viewModel { ProfileScreenViewModel(get()) }
}