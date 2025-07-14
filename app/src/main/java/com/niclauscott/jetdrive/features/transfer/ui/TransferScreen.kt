package com.niclauscott.jetdrive.features.transfer.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.niclauscott.jetdrive.core.database.domain.constant.TransferStatus
import com.niclauscott.jetdrive.core.database.domain.model.Download
import com.niclauscott.jetdrive.core.database.domain.model.Upload
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import com.niclauscott.jetdrive.features.transfer.ui.component.FileTransferTopBar
import java.util.UUID
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.LayoutDirection
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
import com.niclauscott.jetdrive.features.transfer.ui.component.TransferAction
import com.niclauscott.jetdrive.features.transfer.ui.state.TransferScreenUiEvent
import java.util.Random

var minSize = 5L * 1024 * 1024
var maxSize = 10L * 1024 * 1024
var count = 1
val downloads: (String) -> Download = {
    val random = Random()
    val size = minSize + ((random.nextDouble() * (maxSize - minSize))).toLong()
    Download(
        id = UUID.randomUUID(),
        fileId = UUID.randomUUID(),
        fileName = it,
        fileSize = size,
        mimeType = "video/mp4",
        sizePerChunk = 1,
        numberOfChunks = 1,
        downloadedBytes = size / (1..3).random(),
        status = listOf(TransferStatus.PENDING, TransferStatus.ACTIVE, TransferStatus.COMPLETED).random(),
        queuePosition = count++,
        speed = (1..9).random().toDouble(),
        eta = (10..50).random().toDouble()
    )
}
val uploads: (String) -> Upload = {
    val random = Random()
    val size = minSize + ((random.nextDouble() * (maxSize - minSize))).toLong()
    Upload(
        id = UUID.randomUUID(),
        uri = "",
        fileName = it,
        uploadedChunks = listOf(1,3,4,5,6,7,8),
        totalBytes = size,
        uploadedBytes = size / (1..3).random(),
        chunkSize = 1024,
        status = listOf(TransferStatus.PENDING, TransferStatus.ACTIVE, TransferStatus.COMPLETED).random(),
        queuePosition = count++,
        speed = (1..9).random().toDouble(),
        eta = (10..50).random().toDouble()
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DownloadScreenPreview(modifier: Modifier = Modifier) {
    UploadScreen(
        items = (1..10).map { uploads("File $it") },
        onMove = { _, _ ->},
        onToggle = {},
        modifier = Modifier
    )
}

@Composable
fun TransferScreen(
    modifier: Modifier = Modifier,
    landingScreenViewModel: LandingScreenViewModel,
    viewModel: TransferScreenViewModel
) {
    var isCurrentScreenDownload by rememberSaveable { mutableStateOf(true) }
    val state = viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        landingScreenViewModel.hideBottomBars()
        onDispose { landingScreenViewModel.showBottomBars() }
    }

    Scaffold(
        topBar = {
            FileTransferTopBar(
                modifier =  modifier,
                isCurrentScreenDownload = isCurrentScreenDownload,
                isUploadsPaused = state.value.isUploadsPaused,
                isDownloadsPaused = state.value.isDownloadsPaused,
                isAllTransferPaused = state.value.isAllTransferPaused,
                onTransferAction = {
                    when (it) {
                        TransferAction.CancelAllTransfer -> viewModel.onEvent(TransferScreenUiEvent.CancelAllTransfer)
                        TransferAction.ToggleAllTransfer -> viewModel.onEvent(TransferScreenUiEvent.ToggleAllTransfer)
                        is TransferAction.CancelSpecificTransfers -> {
                            viewModel.onEvent(TransferScreenUiEvent.CancelSpecificTransfers(
                                if (isCurrentScreenDownload) TransferType.DOWNLOAD else TransferType.UPLOAD
                            ))
                        }
                        is TransferAction.ToggleSpecificTransfers -> {
                            viewModel.onEvent(TransferScreenUiEvent.ToggleSpecificTransfers(
                                if (isCurrentScreenDownload) TransferType.DOWNLOAD else TransferType.UPLOAD
                            ))
                        }
                    }
                },
                onBackClick = { viewModel.onEvent(TransferScreenUiEvent.GoBack) }
            ) { isCurrentScreenDownload = !isCurrentScreenDownload }
        }
    ) { paddingValues -> paddingValues.calculateTopPadding()

        AnimatedContent(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = paddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateRightPadding(LayoutDirection.Rtl)
            ),
            targetState = isCurrentScreenDownload,
            transitionSpec = {
                val slidDirection = if (!targetState) 1 else -1
                slideInHorizontally { offset -> offset * slidDirection
                } togetherWith slideOutHorizontally { offset -> -offset * slidDirection }
            }
        ) { target ->
            when(target) {
                true -> {
                    DownloadScreen(
                        items = state.value.downloadTasks,
                        onMove = { from, to ->
                            viewModel.onEvent(TransferScreenUiEvent.Move(from, to, TransferType.DOWNLOAD))
                        },
                        onToggle = { id ->
                            viewModel.onEvent(TransferScreenUiEvent.ToggleTransfer(id, TransferType.DOWNLOAD))
                        },
                        modifier = Modifier
                    )
                }
                false -> {
                    UploadScreen(
                        items = state.value.uploadTasks,
                        onMove = { from, to ->
                            viewModel.onEvent(TransferScreenUiEvent.Move(from, to, TransferType.UPLOAD))
                        },
                        onToggle = { id ->
                            viewModel.onEvent(TransferScreenUiEvent.ToggleTransfer(id, TransferType.UPLOAD))
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    }

}

