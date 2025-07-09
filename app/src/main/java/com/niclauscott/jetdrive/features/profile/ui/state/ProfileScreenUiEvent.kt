package com.niclauscott.jetdrive.features.profile.ui.state

sealed interface ProfileScreenUiEvent {
    data class EditProfileName(val firstName: String, val lastName: String): ProfileScreenUiEvent
}