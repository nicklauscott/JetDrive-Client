package com.niclauscott.jetdrive.features.file.di

import com.niclauscott.jetdrive.features.file.data.repository.FileRepositoryImpl
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.ui.screen.copy_move.FileCopyMoveScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.FileScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val fileModule = module {
    factory<FileRepository>{ FileRepositoryImpl(get(), get(), get())  }
    viewModel { param -> FileScreenViewModel(param.get(), param.get(), param.get(), get()) }
    viewModel { param ->
        FileCopyMoveScreenViewModel(
            param.get(), param.get(), param.get(), param.get(), param.get(), param.get(), get()
        )
    }
}