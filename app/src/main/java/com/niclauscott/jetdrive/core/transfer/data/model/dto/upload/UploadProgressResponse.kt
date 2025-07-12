package com.niclauscott.jetdrive.core.transfer.data.model.dto.upload

import kotlinx.serialization.Serializable

@Serializable
data class UploadProgressResponse(
    val uploadedChunks: List<Int>, val totalBytes: Long,
    val uploadedBytes: Long, val chunkSize: Int, val uploadStatus: String
) {
    val missingChunks: List<Int>
        get() = uploadedChunks
}