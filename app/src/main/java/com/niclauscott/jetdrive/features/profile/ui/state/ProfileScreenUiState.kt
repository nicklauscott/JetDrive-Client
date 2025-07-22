package com.niclauscott.jetdrive.features.profile.ui.state

import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import com.niclauscott.jetdrive.features.profile.domain.model.User

data class ProfileScreenUiState(
    val isProfileLoading: Boolean = false,
    val profileError: String? = null,
    val profileData: User? = null,

    val isStatsLoading: Boolean = false,
    val statsError: String? = null,
    val statsData: UserFileStats? = null,

    val isProfileUpdating: Boolean = false,
    val isProfilePictureUpdating: Boolean = false,
)