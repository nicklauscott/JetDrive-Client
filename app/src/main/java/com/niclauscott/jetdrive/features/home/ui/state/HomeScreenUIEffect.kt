package com.niclauscott.jetdrive.features.home.ui.state

interface HomeScreenUIEffect {
    data class ShowSnackBar(val message: String): HomeScreenUIEffect
}