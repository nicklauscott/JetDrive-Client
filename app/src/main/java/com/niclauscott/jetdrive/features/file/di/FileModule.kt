package com.niclauscott.jetdrive.features.file.di

import com.niclauscott.jetdrive.features.file.data.repository.FilePreviewRepositoryImpl
import com.niclauscott.jetdrive.features.file.data.repository.FileRepositoryImpl
import com.niclauscott.jetdrive.features.file.domain.repository.FilePreviewRepository
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.FileCopyMoveScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.FileDetailScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.FileScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.FilePreviewScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.FileSearchScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val fileModule = module {
    factory<FileRepository> { FileRepositoryImpl(get(), get(), get(), get())  }
    factory<FilePreviewRepository> {
        FilePreviewRepositoryImpl(
            get(), get(), get(),
            get(named("unAuthClient")), get(named("file_url"))
        )
    }
    viewModel { param -> FileScreenViewModel(param.get(), param.get(), param.get(), get(), get()) }
    viewModel { param -> FilePreviewScreenViewModel(param.get(), param.get(), param.get(), get()) }
    viewModel { param -> FileSearchScreenViewModel(param.get(), param.get(), get(), get()) }
    viewModel { param -> FileDetailScreenViewModel(param.get(), param.get(), param.get(), get()) }
    viewModel { param ->
        FileCopyMoveScreenViewModel(
            param.get(), param.get(), param.get(), param.get(), param.get(), param.get(), get()
        )
    }
}