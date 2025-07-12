package com.niclauscott.jetdrive.core.transfer.data.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.niclauscott.jetdrive.core.transfer.data.TransferManager
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferRepository
import kotlinx.coroutines.flow.first

class ResumeWorker(
    appContext: Context, workerParams: WorkerParameters,
    private val repository: TransferRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        //repository.getAllIncompleteTransfer().first().forEach(TransferManager::resumeTransfer)
        if (repository.getAllIncompleteTransfer().first().isNotEmpty()) {
            TransferManager.init()
        }
        return Result.success()
    }
}