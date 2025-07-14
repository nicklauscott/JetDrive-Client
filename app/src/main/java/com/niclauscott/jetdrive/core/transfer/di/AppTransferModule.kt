package com.niclauscott.jetdrive.core.transfer.di

import android.content.Context
import androidx.work.WorkerParameters
import com.niclauscott.jetdrive.core.transfer.data.repository.AppAppTransferRepositoryImpl
import com.niclauscott.jetdrive.core.transfer.data.repository.TransferServiceControllerImpl
import com.niclauscott.jetdrive.core.transfer.data.service.ResumeWorker
import com.niclauscott.jetdrive.core.transfer.domain.repository.AppTransferRepository
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferServiceController
import org.koin.dsl.module

val appTransferModule = module {
    factory<AppTransferRepository> { AppAppTransferRepositoryImpl(get()) }
    factory<TransferServiceController> { TransferServiceControllerImpl(get()) }
    factory { (context: Context, params: WorkerParameters) ->
        ResumeWorker(context, params, get())
    }
}