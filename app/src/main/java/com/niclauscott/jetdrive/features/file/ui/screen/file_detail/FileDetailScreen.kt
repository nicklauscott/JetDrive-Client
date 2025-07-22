package com.niclauscott.jetdrive.features.file.ui.screen.file_detail

import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component.FileDetailScreenLandscape
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component.FileDetailScreenPortrait
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component.FileDetailTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state.FileDetailScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state.FileDetailScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DeleteDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.RenameDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel

@Composable
fun FileDetailScreen(
    modifier: Modifier = Modifier,
    landingScreenViewModel: LandingScreenViewModel,
    viewModel: FileDetailScreenViewModel
) {
    val context = LocalContext.current
    val fileNode = viewModel.state.value.fileNode
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var toast by remember { mutableStateOf<Toast?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FileDetailScreenUIEffect.ShowSnackBar -> {
                    toast?.cancel()
                    toast = Toast.makeText(context, effect.message, Toast.LENGTH_SHORT)
                    toast?.show()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        landingScreenViewModel.hideBottomBars()
        onDispose { landingScreenViewModel.showBottomBars() }
    }

    if (showRenameDialog) {
        RenameDialog(
            fileId = fileNode.id,
            fileName = fileNode.name,
            onDismiss = { showRenameDialog = false }
        ) { _, newName ->
            viewModel.onEvent(FileDetailScreenUIEvent.Rename(newName = newName))
            showRenameDialog = false
        }
    }

    if (showDeleteDialog) {
        DeleteDialog(
            fileId = fileNode.id,
            onDismiss = { showDeleteDialog = false }
        ) {
            viewModel.onEvent(FileDetailScreenUIEvent.Delete)
            showDeleteDialog = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            FileDetailTopBar(
                onDownloadClick = { viewModel.onEvent(FileDetailScreenUIEvent.Download) },
                onActionClicked = { action: Action ->
                    when (action) {
                        Action.Rename ->  showRenameDialog = true
                        Action.Move -> viewModel.onEvent(FileDetailScreenUIEvent.Move)
                        Action.Copy -> viewModel.onEvent(FileDetailScreenUIEvent.Copy)
                        Action.Delete -> showDeleteDialog = true
                        else -> {}
                    }
                },
                onBackClick = {
                    viewModel.onEvent(FileDetailScreenUIEvent.GoBack)
                }
            )
        }
    ) {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
        when (deviceConfiguration) {
            DeviceConfiguration.MOBILE_PORTRAIT -> FileDetailScreenPortrait(Modifier, it, fileNode)
            else -> FileDetailScreenLandscape(Modifier, it, fileNode)
        }
    }
}
