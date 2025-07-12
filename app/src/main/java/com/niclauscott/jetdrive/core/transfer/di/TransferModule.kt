package com.niclauscott.jetdrive.core.transfer.di

import android.content.Context
import androidx.work.WorkerParameters
import com.niclauscott.jetdrive.core.transfer.data.repository.TransferRepositoryImpl
import com.niclauscott.jetdrive.core.transfer.data.repository.TransferServiceControllerImpl
import com.niclauscott.jetdrive.core.transfer.data.service.ResumeWorker
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferRepository
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferServiceController
import org.koin.dsl.module

val transferModule = module {
    factory<TransferRepository> { TransferRepositoryImpl(get()) }
    factory<TransferServiceController> { TransferServiceControllerImpl(get()) }
    factory { (context: Context, params: WorkerParameters) ->
        ResumeWorker(context, params, get())
    }
}