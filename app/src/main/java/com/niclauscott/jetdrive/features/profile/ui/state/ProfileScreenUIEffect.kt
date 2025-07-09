package com.niclauscott.jetdrive.features.profile.ui.state

sealed interface ProfileScreenUIEffect {
    data class ShowSnackBar(val message: String): ProfileScreenUIEffect
}