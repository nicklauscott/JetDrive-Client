package com.niclauscott.jetdrive.features.file.domain.util

import com.niclauscott.jetdrive.R

fun getFileIcon(mimeType: String): Int {
    return when (mimeType) {
        "-1" -> R.drawable.file_filled_icon
        "application/vnd.android.package-archive" -> R.drawable.app_icon
        "audio/mpeg", "audio/wav", "audio/ogg" -> R.drawable.audio_icon
        "text/plain", "text/html", "text/css", "text/csv", "text/json", "text/xml" -> R.drawable.docs_icon
        "image/jpg", "image/jpeg", "image/png", "image/gif"  -> R.drawable.image_icon
        "application/zip", "application/vnd.rar", "application/gzip", "application/x-tar", "application/x-7z-compressed"  -> R.drawable.archive_icon
        "video/mp4", "video/webm", "video/x-matroska", "video/x-msvideo", "video/quicktime"  -> R.drawable.movie_icon
        else -> R.drawable.unknown_file_icon
    }
}