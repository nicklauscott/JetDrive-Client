package com.niclauscott.jetdrive.core.transfer.domain.model

data class NotificationProgress(
    val taskId: String,
    val fileName: String,
    val progress: Int?
)
