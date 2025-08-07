package com.niclauscott.jetdrive.features.profile.di

import com.niclauscott.jetdrive.features.profile.data.repository.ProfileRepositoryImpl
import com.niclauscott.jetdrive.features.profile.domain.repository.ProfileRepository
import com.niclauscott.jetdrive.features.profile.ui.ProfileScreenViewModel
import kotlinx.coroutines.FlowPreview
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@OptIn(FlowPreview::class)
val profileModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get(), get()) }
    viewModel { param -> ProfileScreenViewModel(get(), param.get()) }
}