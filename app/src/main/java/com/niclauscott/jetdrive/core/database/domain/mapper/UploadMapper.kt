package com.niclauscott.jetdrive.core.database.domain.mapper

import com.niclauscott.jetdrive.core.database.data.entities.upload.UploadStatus
import com.niclauscott.jetdrive.core.database.domain.model.Upload

fun Upload.toEntity(): UploadStatus =
    UploadStatus(
        id = id,
        uri = uri,
        fileName = fileName,
        uploadedChunks = uploadedChunks,
        totalBytes = totalBytes,
        uploadedBytes = uploadedBytes,
        chunkSize = chunkSize,
        status = status,
        queuePosition = queuePosition
    )

fun UploadStatus.toModel(): Upload =
    Upload(
        id = id,
        uri = uri,
        fileName = fileName,
        uploadedChunks = uploadedChunks,
        totalBytes = totalBytes,
        uploadedBytes = uploadedBytes,
        chunkSize = chunkSize,
        status = status,
        queuePosition = queuePosition,
        speed = speed,
        eta = eta
    )