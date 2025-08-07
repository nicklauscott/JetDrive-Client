package com.niclauscott.jetdrive.features.profile.ui.state

sealed interface ProfileScreenUiEvent {
    data class EditProfileName(val firstName: String, val lastName: String): ProfileScreenUiEvent
    data class UploadProfilePicture(val uri: String): ProfileScreenUiEvent
    data object Logout: ProfileScreenUiEvent
}