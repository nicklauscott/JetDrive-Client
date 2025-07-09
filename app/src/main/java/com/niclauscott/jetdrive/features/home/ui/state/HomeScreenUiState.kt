package com.niclauscott.jetdrive.features.home.ui.state

import com.niclauscott.jetdrive.core.domain.model.UserFileStats

data class HomeScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: UserFileStats? = null
)
