package com.niclauscott.jetdrive.features.transfer.di

import com.niclauscott.jetdrive.features.transfer.data.repository.TransferRepositoryImpl
import com.niclauscott.jetdrive.features.transfer.domain.repository.TransferRepository
import com.niclauscott.jetdrive.features.transfer.ui.TransferScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val transferModule = module {
    factory<TransferRepository> { TransferRepositoryImpl(get(), get()) }
    viewModel { param -> TransferScreenViewModel(param.get(), param.get(), get()) }
}