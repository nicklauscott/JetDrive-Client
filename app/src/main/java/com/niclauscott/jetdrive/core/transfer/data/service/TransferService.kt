package com.niclauscott.jetdrive.core.transfer.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.transfer.data.TransferManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TransferService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(1, createNotification())
        TransferManager.init()
        serviceScope.launch {
            TransferManager.notificationProgress.collect { progress ->
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (progress.progress != null) {
                    notificationManager.notify(1, createProgressNotification(progress.fileName, progress.progress))
                } else {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    notificationManager.cancel(1)
                 }
            }
        }
        return START_STICKY
    }

    private fun createProgressNotification(
        title: String = "Transferring", progress: Int = 10, indeterminate: Boolean = false
    ): Notification {
        return NotificationCompat.Builder(this, "transfer_channel")
            .setContentTitle(title)
            .setSmallIcon(R.drawable.jet_drive_logo)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, indeterminate)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "transfer_channel", "File Transfers", NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "transfer_channel")
            .setContentTitle("Transferring...")
            .setSmallIcon(R.drawable.jet_drive_logo)
            .build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}