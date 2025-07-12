package com.niclauscott.jetdrive.core.transfer.data.repository

import android.content.Context
import android.content.Intent
import com.niclauscott.jetdrive.core.transfer.data.service.TransferService
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferServiceController

class TransferServiceControllerImpl(private val context: Context): TransferServiceController {
    override fun ensureServiceRunning() {
        val intent = Intent(context, TransferService::class.java)
        context.startForegroundService(intent)
    }
}