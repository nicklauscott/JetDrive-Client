package com.niclauscott.jetdrive.features.transfer.domain.model.constant

fun calculatePercentage(bytes: Long, totalBytes: Long): Int {
    val percent = (bytes.toDouble() / totalBytes .toDouble()) * 100
    return percent.coerceAtMost(100.0).toInt()
}

fun formatFileSize(bytes: Long): String {
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

fun formatEta(etaSeconds: Double): String {
    return when {
        etaSeconds < 60 -> "${etaSeconds.toInt()}s"
        etaSeconds < 3600 -> "${(etaSeconds / 60).toInt()}m ${(etaSeconds % 60).toInt()}s"
        else -> "${(etaSeconds / 3600).toInt()}h ${((etaSeconds % 3600) / 60).toInt()}m"
    }
}