package com.niclauscott.jetdrive.features.home.ui.state

sealed interface HomeScreenUIEvent {
    data class CreateNewFolder(val folderName: String) : HomeScreenUIEvent
    data object UploadFile : HomeScreenUIEvent
}