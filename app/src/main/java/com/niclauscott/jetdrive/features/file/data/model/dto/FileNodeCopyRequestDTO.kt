package com.niclauscott.jetdrive.features.file.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileNodeCopyRequestDTO(
    val id: String,
    val parentId: String?
)
