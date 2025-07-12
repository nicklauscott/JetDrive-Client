package com.niclauscott.jetdrive.core.transfer.data.model.dto.upload

import kotlinx.serialization.Serializable

@Serializable
data class UploadInitiateResponse(val uploadId: String, val chunkSize: Int)
