package com.niclauscott.jetdrive.features.profile.domain.repository

import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import com.niclauscott.jetdrive.features.profile.domain.constant.ProfileResponse
import com.niclauscott.jetdrive.features.profile.domain.model.User

interface ProfileRepository {
    suspend fun getProfile(): ProfileResponse<User>
    suspend fun getFileStats(): ProfileResponse<UserFileStats>
    suspend fun updateProfileName(firstName: String, lastName: String): ProfileResponse<User>
    suspend fun uploadProfilePhoto(uri: String): ProfileResponse<User>
    suspend fun logout()
}