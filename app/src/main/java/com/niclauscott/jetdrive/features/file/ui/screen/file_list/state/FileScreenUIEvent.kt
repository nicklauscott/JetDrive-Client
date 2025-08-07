package com.niclauscott.jetdrive.features.file.ui.screen.file_list.state

import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.features.file.domain.model.FileNode

sealed interface FileScreenUIEvent {
    data object GoBack: FileScreenUIEvent
    data class OpenFileNode(val fileNode: FileNode): FileScreenUIEvent
    data class Sort(val sortOrder: SortOrder, val sortType: SortType): FileScreenUIEvent
    data class FileDetails(val fileNode: FileNode): FileScreenUIEvent
    data class Download(val fileNode: FileNode): FileScreenUIEvent
    data class Rename(val fileId: String, val newName: String): FileScreenUIEvent
    data class Move(val fileNode: FileNode): FileScreenUIEvent
    data class Copy(val fileNode: FileNode): FileScreenUIEvent
    data class Delete(val fileId: String): FileScreenUIEvent
    data class CreateNewFolder(val folderName: String) : FileScreenUIEvent
    data class CreateNewFile(val folderName: String) : FileScreenUIEvent
    data class UploadFile(val uri: String): FileScreenUIEvent
    data object RefreshOnAppear: FileScreenUIEvent
    data object RefreshData: FileScreenUIEvent
    data object Search : FileScreenUIEvent
    data object CancelDownload : FileScreenUIEvent
    data object OpenTransferScreen: FileScreenUIEvent
}

enum class SortType {
    Name, Date, Type, Size
}

enum class SortOrder {
    ASC, DESC
}

enum class ActionType {
    ModifyingAction, DangerousAction, OtherAction
}

enum class Action(val icon: Int, val actionType: ActionType) {
    Rename(R.drawable.rename_icon, ActionType.ModifyingAction),
    Move(R.drawable.move_icon, ActionType.ModifyingAction),
    Copy(R.drawable.copy_icon, ActionType.ModifyingAction),
    Delete(R.drawable.delete_icon, ActionType.DangerousAction),
    Info(R.drawable.info_icon, ActionType.OtherAction),
    Download(R.drawable.download_icon, ActionType.OtherAction),
}