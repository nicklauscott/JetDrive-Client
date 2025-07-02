package com.niclauscott.jetdrive.features.auth.domain.repository

import android.content.Context
import com.niclauscott.jetdrive.features.auth.domain.model.constant.AuthResponse

interface OAuthClient {
    suspend fun login(context: Context, jetDriveLogin: () -> Unit): AuthResponse
}