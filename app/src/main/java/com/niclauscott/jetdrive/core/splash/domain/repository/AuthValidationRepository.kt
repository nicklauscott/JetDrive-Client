package com.niclauscott.jetdrive.core.splash.domain.repository

import com.niclauscott.jetdrive.core.splash.domain.model.constant.AuthValidationResponse

interface AuthValidationRepository {
    suspend fun validate(): AuthValidationResponse
}