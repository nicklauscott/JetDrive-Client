package com.niclauscott.jetdrive.features.transfer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import com.niclauscott.jetdrive.features.transfer.ui.component.FileTransferTopBar
import com.niclauscott.jetdrive.features.transfer.ui.component.TransferAction
import com.niclauscott.jetdrive.features.transfer.ui.state.TransferScreenUiEvent

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

