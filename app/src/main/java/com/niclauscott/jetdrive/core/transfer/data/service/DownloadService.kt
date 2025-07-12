package com.niclauscott.jetdrive.core.transfer.data.service

import android.content.Context
import android.os.Environment
import com.niclauscott.jetdrive.core.database.data.entities.TransferStatus
import com.niclauscott.jetdrive.core.database.data.entities.downloads.DownloadStatus
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class DownloadService(
    baseUrl: String, private val client: HttpClient,
    context: Context, private val downloadStatus: DownloadStatus,
    private val repository: TransferRepository,
) {
    private val chunkSize = 1024 * 1024L
    private val downloadUrl: (String) -> String = { "$baseUrl/files/download/$it" }

    private val recentSpeed = mutableListOf<Double>()
    private var run = AtomicBoolean(true)

    private var tempDir: File = File(context.filesDir, "JetDrive/.tmp")
    private val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    private val finalFile = File(
        downloadsDir,
        "JetDrive/${getDirectoryFromMimeTye(downloadStatus.mimeType)}/${downloadStatus.fileName}"
    )

    init { if (!tempDir.exists()) tempDir.mkdirs() }

    private fun calculateSpeed(elapsedSeconds: Double, chunkSize: Long): Double {
        return chunkSize / 1024.0 / 1024.0 / elapsedSeconds
    }

    private fun updateSpeedDisplay(speed: Double): Double {
        recentSpeed.add(speed)
        if (recentSpeed.size > 5) recentSpeed.removeAt(0)
        return if (recentSpeed.size < 2) speed else recentSpeed.average()
    }

    private fun eta(totalBytes: Long, uploadedBytes: Long, averageSpeedMBps: Double): Double {
        val remainingBytes = totalBytes - uploadedBytes
        return remainingBytes / (averageSpeedMBps * 1024 * 1024)
    }

    suspend fun init() {
         val status = repository.saveDownloadStatus(
            downloadStatus.copy(
                sizePerChunk = chunkSize, status = TransferStatus.ACTIVE,
                numberOfChunks = ((downloadStatus.fileSize) / chunkSize).toInt(),
            )
        )
        download(status)
    }

    suspend fun download(status: DownloadStatus) {
        val tempDir = File(tempDir, status.fileName)
        if (!tempDir.exists()) tempDir.mkdirs()

        val total = status.fileSize
        var start = 0L
        var end: Long
        var chunkIndex = 1

        while (start < total) {
            end = (start + chunkSize - 1).coerceAtMost(total - 1)
            val currentChunk = status.downloadedChunks.find { it == chunkIndex }
            if (currentChunk == null) {
                val startTime = System.nanoTime()

                val response: HttpResponse = client.get(downloadUrl(status.fileId.toString())) {
                    url { parameters.append("mode", "stream") }
                    headers { append(HttpHeaders.Range, "bytes=$start-$end") }
                }

                val tempFile = File(tempDir, "jet_drive_download_$chunkIndex.part")
                response.bodyAsChannel().toInputStream().use { input ->
                    tempFile.outputStream().use { output ->
                        val bytesCopied = input.copyTo(output)

                        val dbDownloadStatus = repository.getDownloadStatusById(status.id) ?: status
                        val elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0
                        val speed = calculateSpeed(elapsedTime, chunkSize = bytesCopied)
                        val eta = eta(status.fileSize, dbDownloadStatus.downloadedBytes, speed)

                        val updatedDownloadStatus = dbDownloadStatus.copy(
                            speed = updateSpeedDisplay(speed), eta = eta,
                            downloadedBytes = dbDownloadStatus.downloadedBytes + bytesCopied,
                            downloadedChunks = dbDownloadStatus.downloadedChunks + chunkIndex
                        )
                        repository.saveDownloadStatus(updatedDownloadStatus)
                    }
                }
            }
            start = end + 1
            chunkIndex++
        }

        val dbDownloadStatus = repository.getDownloadStatusById(downloadStatus.id)
        if (dbDownloadStatus != null && !dbDownloadStatus.isComplete) return

        mergeFiles(finalFile, tempDir)
        deleteDirectoryRecursively(tempDir)
        dbDownloadStatus?.let { repository.saveDownloadStatus(it.copy(status = TransferStatus.COMPLETED)) }
    }

    private fun mergeFiles(finalFile: File, tempDir: File) {
        finalFile.parentFile?.mkdirs()
        finalFile.outputStream().use { output ->
            tempDir
                .listFiles()
                ?.filter { it.isFile }
                ?.sortedBy { it.name } // assumes chunks are named in order like "part1", "part2"...
                ?.forEach { part ->
                    part.inputStream().use { it.copyTo(output) }
                }
        }
    }

    private fun deleteDirectoryRecursively(dir: File) {
        if (!dir.exists()) return
        dir.walkBottomUp().forEach { file ->
            file.delete()
        }
    }

    private fun getDirectoryFromMimeTye(mimeType: String): String {
        return when (mimeType.split("/")[0]) {
           "video" -> "Video"
           "audio" -> "Music"
           "image" -> "Photos"
           "text" -> "Document"
            else -> "Others"
        }
    }
}