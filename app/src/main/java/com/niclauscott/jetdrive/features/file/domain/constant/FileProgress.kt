package com.niclauscott.jetdrive.features.file.domain.constant

sealed class FileProgress<out T> {
    data object Idle : FileProgress<Nothing>()
    data class Loading(val percent: Float?) : FileProgress<Nothing>()
    data class Success<T>(val data: T) : FileProgress<T>()
    data class Failure(val error: String) : FileProgress<Nothing>()
}
