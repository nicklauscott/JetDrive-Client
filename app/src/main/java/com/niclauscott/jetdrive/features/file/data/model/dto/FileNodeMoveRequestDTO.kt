package com.niclauscott.jetdrive.features.file.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileNodeMoveRequestDTO(
    val id: String,
    val newParentId: String?
)
