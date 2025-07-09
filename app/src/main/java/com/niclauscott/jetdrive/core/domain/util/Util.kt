package com.niclauscott.jetdrive.core.domain.util

val TAG: (String) -> String = {
    "JET_DRIVE_APP -> $it"
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
