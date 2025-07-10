package com.niclauscott.jetdrive.features.file.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import kotlinx.serialization.Serializable

@Serializable
data class FileListScreen(val fileNode: FileNode): NavKey

@Serializable
data object SearchScreen: NavKey

@Serializable
data class DetailScreen(val fileNode: FileNode): NavKey

@Serializable
data class PreviewScreen(val fileNode: FileNode): NavKey

@Serializable
data class CopyMoveScreen(
    val fileNode: FileNode,
    val folderId: String,
    val folderName: String,
    val action: Action
): NavKey