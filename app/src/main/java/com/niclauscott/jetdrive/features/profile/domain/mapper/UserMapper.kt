package com.niclauscott.jetdrive.features.profile.domain.mapper

import com.niclauscott.jetdrive.features.profile.data.model.dto.UpdateUserRequestDTO
import com.niclauscott.jetdrive.features.profile.data.model.dto.UserResponseDTO
import com.niclauscott.jetdrive.features.profile.domain.model.UpdateUser
import com.niclauscott.jetdrive.features.profile.domain.model.User

fun UserResponseDTO.toUser(): User =
    User(
        email = email ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        picUrl = picUrl
    )

fun UpdateUser.toDTO(): UpdateUserRequestDTO =
    UpdateUserRequestDTO(
        email = email,
        firstName = firstName,
        lastName = lastName
    )