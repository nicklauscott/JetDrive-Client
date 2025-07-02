package com.niclauscott.jetdrive.features.file.domain.constant

sealed interface FileResponse<T> {
    data class Successful<T>(val data: T): FileResponse<T>
    data class Failure<T>(val message: String): FileResponse<T>
}