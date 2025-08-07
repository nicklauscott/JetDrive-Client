package com.niclauscott.jetdrive.features.file.ui.screen.file_preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.AudioPlayer
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.CloudImageViewer
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.LandscapeFilePreviewScreenTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.PortraitFilePreviewScreenTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component.VideoPreview
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.state.FilePreviewUiEvent
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import kotlinx.coroutines.delay

enum class FileState {
    Loading, Successful, Failed, Idle, None
}


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
    var showTopBar by rememberSaveable { mutableStateOf(true) }
    var fileState by rememberSaveable { mutableStateOf(FileState.Idle) }
    val defaultColor = MaterialTheme.colorScheme.background
    var dominantColor by remember { mutableStateOf(defaultColor) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(FilePreviewUiEvent.ConfigurationChanged)
    }

    LaunchedEffect(Unit) {
        if (state.value.fileType != "audio") {
            delay(1000)
            showTopBar = false
        } else {
            fileState = FileState.None
        }
    }

    DisposableEffect(Unit) {
        onDispose { landingScreenViewModel.showBottomBars() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        dominantColor,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.statusBars,
            topBar = {
                AnimatedVisibility(
                    showTopBar,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {
                    when (deviceConfiguration) {
                        DeviceConfiguration.MOBILE_PORTRAIT -> {
                            if (listOf("image", "audio").contains(state.value.fileType)) {
                                PortraitFilePreviewScreenTopBar(
                                    state = fileState,
                                    title = if (state.value.fileType == "image") state.value.fileNode.name else "",
                                ) {
                                    viewModel.onEvent(FilePreviewUiEvent.GoBack)
                                }
                            }
                        }
                        else -> {
                            if (listOf("image", "audio").contains(state.value.fileType)) {
                                LandscapeFilePreviewScreenTopBar(
                                    state = fileState,
                                    title = if (state.value.fileType == "image") state.value.fileNode.name else "",
                                ) {
                                    viewModel.onEvent(FilePreviewUiEvent.GoBack)
                                }
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            val innerModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(WindowInsets.navigationBars)
            when (state.value.fileType) {
                "image" -> {
                    CloudImageViewer(
                        modifier = Modifier.fillMaxSize(),
                        state.value.isFileUrlLoading, state.value.fileUrl,
                        onStatus = { fileState = it }
                    ) { showTopBar = !showTopBar }
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
                        state.value.audioMetadata,
                        onAverageColorCalculated = { dominantColor = it }
                    ) { viewModel.onEvent(FilePreviewUiEvent.Release) }
                }
            }
        }
    }
}

