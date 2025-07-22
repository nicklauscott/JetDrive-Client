package com.niclauscott.jetdrive.core.sync.domain.service

import com.niclauscott.jetdrive.features.file.domain.model.FileNode

sealed class FileSystemEvent {
    data class FileCreated(val fileNode: FileNode?, val parentId: String?): FileSystemEvent()
    data class FileModified(val fileNode: FileNode, val parentId: String?): FileSystemEvent()
    data class FileDelete(val fileNode: FileNode, val parentId: String?): FileSystemEvent()
    data class FileCopied(val fileNode: FileNode, val newParentId: String?): FileSystemEvent()
    data class FileMoved(val fileNode: FileNode, val oldParentId: String?, val newParentId: String?): FileSystemEvent()
}