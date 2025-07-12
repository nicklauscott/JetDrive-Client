package com.niclauscott.jetdrive.core.transfer.data.model.dto.upload

import kotlinx.serialization.Serializable

@Serializable
data class UploadInitiateRequest(
    val fileName: String, val fileSize: Long,
    val parentId: String? = null, val hasThumbnail: Boolean = false
)