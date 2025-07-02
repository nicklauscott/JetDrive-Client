package com.niclauscott.jetdrive.features.file.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileNodeCreateRequestDTO(
    val name: String,
    val type: String = "folder",
    val parentId: String?
)
