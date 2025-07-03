package com.niclauscott.jetdrive.features.landing.ui.state

import com.niclauscott.jetdrive.R

sealed interface LandingScreenUiEvent {
    data class CreateTextFile(val name: String): LandingScreenUiEvent
    data class CreateFolder(val name: String): LandingScreenUiEvent
    data object UploadFile: LandingScreenUiEvent
}

enum class FileActions(val description: String, val icon: Int) {
    CreateFolder("Create folder", R.drawable.create_folder_icon),
    //CreateTextFile("Create text file", R.drawable.create_folder_icon),
    UploadFile("Upload file", R.drawable.file_upload_icon)
}