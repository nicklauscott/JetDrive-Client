package com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.component.CustomSnackbarHost
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.component.CreateNewFolderDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.component.FileCopyMoveNodeCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.component.FileCopyMoveTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state.FileCopyMoveScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state.FileCopyMoveScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.TextButton
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action

@Composable
fun FileCopyMoveScreen(
    modifier: Modifier = Modifier,
    action: Action,
    viewModel: FileCopyMoveScreenViewModel,
) {
    val context = LocalContext.current
    val state = viewModel.state

    var showCreateFolderDialog by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onEvent(FileCopyMoveScreenUIEvent.RefreshData)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FileCopyMoveScreenUIEffect.ShowSnackbar -> {
                    Log.e("SplashScreenViewModel", "LoginScreen -> effect: ${effect.message}")
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    if (showCreateFolderDialog) {
        CreateNewFolderDialog(
            onDismiss = { showCreateFolderDialog = false }
        ) { folderName ->
            viewModel.onEvent(FileCopyMoveScreenUIEvent.CreateNewFolder(folderName))
            showCreateFolderDialog = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            CustomSnackbarHost(snackbarHostState = snackbarHostState)
        },
        topBar = {
            FileCopyMoveTopBar(
                title = state.value.title,
                isRoot = state.value.parentId == null,
                onCreateFolderClick = { showCreateFolderDialog = true },
                onBackClick = {
                    viewModel.onEvent(FileCopyMoveScreenUIEvent.GoBack)
                }
            )
        },
        bottomBar = {
            Row(
                modifier = modifier.fillMaxWidth()
                    .padding(vertical = 3.percentOfScreenHeight(), horizontal = 3.percentOfScreenWidth()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    label = getString(context, R.string.cancel),
                    backgroundColor = Color.Transparent,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    enabled = true,
                    texStyle = MaterialTheme.typography.bodyLarge,
                    onClick = {  viewModel.onEvent(FileCopyMoveScreenUIEvent.Cancel) }
                )
                Spacer(modifier = Modifier.width(3.percentOfScreenWidth()))
                val enabled = (state.value.parentId != state.value.fileNode.parentId)
                        || action == Action.Copy
                TextButton(
                    label = getString(context, if (action == Action.Copy) R.string.copy else R.string.move),
                    enabled = enabled,
                    texStyle = MaterialTheme.typography.bodyLarge,
                    backgroundColor = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textColor = MaterialTheme.colorScheme.background
                        .copy(alpha = if (enabled) 1f else 0.6f),
                    onClick = { viewModel.onEvent(FileCopyMoveScreenUIEvent.Complete) }
                )
            }
        }
    ) { paddingValues ->

        when {
            state.value.isLoadingFiles == true -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                return@Scaffold
            }

            state.value.isLoadingFiles == false && state.value.errorMessage != null ->  {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.error_icon),
                        contentDescription = getString(context, R.string.upload_icon),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))
                    Text(
                        viewModel.state.value.errorMessage ?: getString(context, R.string.unknown_error),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
                return@Scaffold
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(WindowInsets.navigationBars)
                ) {

                    items(state.value.children) {
                        FileCopyMoveNodeCell(fileNode = it) { folderId, folderName ->
                            viewModel.onEvent(FileCopyMoveScreenUIEvent.OpenFolderNode(
                                fileNode = state.value.fileNode,
                                folderId = folderId,
                                folderName = folderName,
                                action = action
                            ))
                        }
                    }
                }
            }
        }

    }

}