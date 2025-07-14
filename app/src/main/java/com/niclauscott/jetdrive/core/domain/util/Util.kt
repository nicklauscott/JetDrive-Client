package com.niclauscott.jetdrive.core.domain.util

import android.content.Context
import android.content.Intent
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.niclauscott.jetdrive.core.domain.model.UriData
import java.io.File

val TAG: (String) -> String = {
    "JET___DRIVE___APP -> $it"
}

fun openFileFromCache(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(
        context, // This MUST be activity context
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Open with"))
}

fun getNetworkErrorMessage(e: Throwable): String {
    return when (e) {
        is io.ktor.client.network.sockets.SocketTimeoutException -> "Connection timed out. Please try again."
        is io.ktor.client.network.sockets.ConnectTimeoutException -> "Unable to connect. Check your internet connection."
        is io.ktor.client.plugins.ClientRequestException -> when (e.response.status.value) {
            401 -> "Unauthorized. Please log in again."
            403 -> "Access denied. You don’t have permission."
            404 -> "Resource not found."
            else -> "Client error: ${e.response.status.description}"
        }
        is io.ktor.client.plugins.ServerResponseException ->
            "Server error: ${e.response.status.description}. Try again later."
        is io.ktor.client.plugins.ResponseException ->
            "Unexpected response: ${e.response.status.description}"
        is java.net.UnknownHostException -> "No internet connection. Please check your network."
        is java.net.SocketException -> "Network error occurred."
        else -> "Something went wrong. Please try again."
    }
}

fun getFileInfo(uri: String, context: Context): UriData? {
    val cursor = context.contentResolver.query(
        uri.toUri(),
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null, null, null,
    ) ?: return null

    cursor.use {
        if (it.moveToFirst()) {
            val name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            val size = it.getLong(it.getColumnIndexOrThrow(OpenableColumns.SIZE))
            return UriData(name, size)
        }
    }
    return null
}