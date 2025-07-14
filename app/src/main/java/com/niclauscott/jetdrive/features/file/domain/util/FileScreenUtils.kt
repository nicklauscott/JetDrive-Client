package com.niclauscott.jetdrive.features.file.domain.util

import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.features.file.domain.model.FileNode

fun getFileIcon(mimeType: String): Int {
    return when (mimeType) {
        "-1" -> R.drawable.file_filled_icon
        "application/vnd.android.package-archive" -> R.drawable.app_icon
        "audio/mpeg", "audio/wav", "audio/ogg", "audio/mp3" -> R.drawable.audio_icon
        "text/plain", "text/html", "text/css", "text/csv", "text/json", "text/xml" -> R.drawable.docs_icon
        "image/jpg", "image/jpeg", "image/png", "image/gif"  -> R.drawable.image_icon
        "application/zip", "application/vnd.rar", "application/gzip", "application/x-tar", "application/x-7z-compressed"  -> R.drawable.archive_icon
        "video/mp4", "video/webm", "video/x-matroska", "video/x-msvideo", "video/quicktime"  -> R.drawable.movie_icon
        else -> R.drawable.unknown_file_icon
    }
}

fun getFileIconFromFileName(filename: String): Int {
    val extension = filename.substring(filename.lastIndexOf('.') + 1).lowercase()
    return when (extension) {
       in listOf("mpeg", "wav", "ogg", "mp3") -> R.drawable.audio_icon
       in listOf("plain", "html", "css", "csv", "json", "txt", "xml", "docs") -> R.drawable.docs_icon
       in listOf("jpg", "jpeg", "png", "gif") -> R.drawable.image_icon
       in listOf("mp4", "webm", "x-matroska", "x-msvideo", "quicktime") -> R.drawable.movie_icon
       in listOf("gzip", "rar", "zip", "7zip", "tar", "x-7z-compressed", "gzip") -> R.drawable.archive_icon
       else -> R.drawable.unknown_file_icon
    }
}


fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}


fun shouldOpenFile(fileNode: FileNode): Boolean? {
    val fileExtension = fileNode.mimeType?.split("/")?.get(0) ?: return null
    return listOf("video", "image", "audio").contains(fileExtension)
}