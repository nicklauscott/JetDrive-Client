package com.niclauscott.jetdrive.features.profile.domain.constant

sealed interface ProfileResponse<T> {
    data class Successful<T>(val data: T): ProfileResponse<T>
    data class Failure<T>(val message: String): ProfileResponse<T>
}