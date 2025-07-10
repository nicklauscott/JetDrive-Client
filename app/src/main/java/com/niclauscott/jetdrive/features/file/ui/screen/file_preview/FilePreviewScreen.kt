package com.niclauscott.jetdrive.features.file.ui.screen.file_preview

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.AudioPlayer
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.CloudImageViewer
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.LandscapeFilePreviewScreenTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.PortraitFilePreviewScreenTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.VideoPreview
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.state.FilePreviewUiEvent
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel

@Composable
fun FilePreviewScreen(
    modifier: Modifier = Modifier,
    landingScreenViewModel: LandingScreenViewModel,
    viewModel: FilePreviewScreenViewModel
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    val context = LocalContext.current
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.onEvent(FilePreviewUiEvent.ConfigurationChanged)
    }

    DisposableEffect(Unit) {
        onDispose { landingScreenViewModel.showBottomBars() }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            when (deviceConfiguration) {
                DeviceConfiguration.MOBILE_PORTRAIT -> {
                    if (listOf("image", "audio").contains(state.value.fileType)) {
                        PortraitFilePreviewScreenTopBar(
                            title = if (state.value.fileType == "image") state.value.fileNode.name else "",
                        ) {
                            viewModel.onEvent(FilePreviewUiEvent.GoBack)
                        }
                    }
                }
                else -> {
                    if (listOf("image", "audio").contains(state.value.fileType)) {
                        LandscapeFilePreviewScreenTopBar(
                            title = if (state.value.fileType == "image") state.value.fileNode.name else "",
                        ) {
                            viewModel.onEvent(FilePreviewUiEvent.GoBack)
                        }
                    }
                }
            }

        }
    ) { innerPadding ->
        val innerModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(WindowInsets.navigationBars)
        when (state.value.fileType) {
            "image" -> {
                CloudImageViewer(
                    modifier = innerModifier,
                    state.value.isFileUrlLoading, state.value.fileUrl
                )
                return@Scaffold
            }
            "video" -> {
                viewModel.onEvent(FilePreviewUiEvent.InitializePlayer(context))
                val player = viewModel.playerState.collectAsState()
                VideoPreview(player = player.value) {
                    viewModel.onEvent(FilePreviewUiEvent.Release)
                }
            }
            else -> {
                viewModel.onEvent(FilePreviewUiEvent.InitializePlayer(context))
                val player = viewModel.playerState.collectAsState()
                AudioPlayer(
                    modifier = innerModifier,
                    deviceConfiguration = deviceConfiguration,
                    player = player.value,
                    state.value.fileNode,
                    state.value.audioMetadata
                ) { viewModel.onEvent(FilePreviewUiEvent.Release) }
            }
        }
    }
}

