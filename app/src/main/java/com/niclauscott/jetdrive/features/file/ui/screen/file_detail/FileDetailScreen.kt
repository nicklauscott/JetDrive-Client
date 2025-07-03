package com.niclauscott.jetdrive.features.file.ui.screen.file_detail

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.component.CustomSnackbarHost
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.util.getFileIcon
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component.DetailCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component.FileDetailTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state.FileDetailScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state.FileDetailScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DeleteDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.RenameDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action

@Composable
fun FileDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: FileDetailScreenViewModel
) {
    val context = LocalContext.current

    val fileNode = viewModel.state.value.fileNode
    val fileIcon by remember { mutableIntStateOf(getFileIcon(fileNode.mimeType ?: "-1")) }
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FileDetailScreenUIEffect.ShowSnackBar -> {
                    Log.e("SplashScreenViewModel", "LoginScreen -> effect: ${effect.message}")
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
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
        snackbarHost = {
            CustomSnackbarHost(snackbarHostState = snackbarHostState)
        },
        topBar = {
            FileDetailTopBar(
                onDownloadClick = {},
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
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 3.percentOfScreenWidth())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(fileIcon),
                    contentDescription = getString(context, R.string.file_icon),
                    tint = if (fileNode.type == FileNode.Companion.FileType.Folder) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.width(1.percentOfScreenWidth()))

                Text(
                    text = fileNode.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.width(1.percentOfScreenHeight()))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                thickness = 0.5.dp
            )

            DetailCell(label = "Size", value = fileNode.fileSize)
            DetailCell(label = "Added", value = fileNode.createdDate)
            DetailCell(label = "Last Modified", value = fileNode.updatedDate)
        }
    }
}
