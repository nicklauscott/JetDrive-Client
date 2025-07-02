package com.niclauscott.jetdrive.features.file.domain.model

import com.niclauscott.jetdrive.core.model.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Serializable
data class FileNode(
    val id: String = "-1",
    val name: String = "Drive",
    val type: FileType = FileType.Unknown,
    private val size: Long = 0L,
    val parentId: String? = null,
    val hasThumbnail: Boolean = false,
    val mimeType: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    private val createdAt: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    private val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val createdDate = formatDateTime(createdAt)
    val updatedDate = formatDateTime(updatedAt)
    val fileSize: String = formatFileSize(size)

    private fun formatDateTime(
        dateTime: LocalDateTime,
        pattern: String = "dd MMM yyyy HH:mm",
        locale: Locale = Locale.getDefault()
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return dateTime.format(formatter)
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val units = arrayOf("KB", "MB", "GB", "TB", "PB", "EB")
        var size = bytes.toDouble() / 1024
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.lastIndex) {
            size /= 1024
            unitIndex++
        }

        return String.format("%.1f %s", size, units[unitIndex])
    }


    companion object {
        enum class FileType {
            File, Folder, Unknown
        }

        fun toFileType(type: String): FileType {
            return when(type) {
                "folder" -> FileType.Folder
                "file" -> FileType.File
                else -> FileType.Unknown
            }
        }
    }
}


