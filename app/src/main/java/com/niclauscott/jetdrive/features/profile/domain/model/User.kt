package com.niclauscott.jetdrive.features.profile.domain.model

data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    val picUrl: String?,
)
