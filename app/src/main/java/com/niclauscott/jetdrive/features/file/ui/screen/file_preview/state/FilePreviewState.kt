package com.niclauscott.jetdrive.features.file.ui.screen.file_preview.state

import com.niclauscott.jetdrive.features.file.domain.model.AudioMetadata
import com.niclauscott.jetdrive.features.file.domain.model.FileNode

data class FilePreviewState(
    val fileNode: FileNode,
    val fileUrl: String? = null,
    val isFileUrlLoading: Boolean = false,
    val error: String? = null,

    val audioMetadata: AudioMetadata? = null
) {
    val fileType: String
    get() = fileNode.mimeType?.split("/")?.get(0) ?: ""
}
