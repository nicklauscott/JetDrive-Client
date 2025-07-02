package com.niclauscott.jetdrive.features.file.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileNodeRenameRequestDTO(
    val name: String,
    val newName: String
)
