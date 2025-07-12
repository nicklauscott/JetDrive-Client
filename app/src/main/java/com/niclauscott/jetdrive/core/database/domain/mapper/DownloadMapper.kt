package com.niclauscott.jetdrive.core.database.domain.mapper

import com.niclauscott.jetdrive.core.database.data.entities.downloads.DownloadStatus
import com.niclauscott.jetdrive.core.database.domain.model.Download

fun Download.toEntity(): DownloadStatus =
    DownloadStatus(
        id = id,
        fileId = fileId,
        fileName = fileName,
        fileSize = fileSize,
        sizePerChunk = sizePerChunk,
        numberOfChunks = numberOfChunks,
        status = status,
        mimeType = mimeType,
        queuePosition = queuePosition
    )

fun DownloadStatus.toModel():  Download =
    Download(
        id = id,
        fileId = fileId,
        fileName = fileName,
        fileSize = fileSize,
        sizePerChunk = sizePerChunk,
        numberOfChunks = numberOfChunks,
        status = status,
        mimeType = mimeType,
        queuePosition = queuePosition,
        speed = speed,
        eta = eta
    )