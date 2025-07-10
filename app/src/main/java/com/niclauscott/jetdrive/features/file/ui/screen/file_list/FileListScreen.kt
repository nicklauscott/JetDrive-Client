package com.niclauscott.jetdrive.features.file.ui.screen.file_list

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.domain.util.openFileFromCache
import com.niclauscott.jetdrive.core.ui.component.CustomSnackbarHost
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.constant.FileProgress
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.component.CreateNewFolderDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.component.CreateNewTextFilDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DeleteDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DownloadProgressDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileNodeCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileScreenBottomSheet
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileScreenTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.RenameDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.SortingAndOrderCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortOrder
import com.niclauscott.jetdrive.features.landing.ui.components.ActionsBottomSheet
import com.niclauscott.jetdrive.features.landing.ui.components.FAB
import com.niclauscott.jetdrive.features.landing.ui.state.FileActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(modifier: Modifier = Modifier, viewModel: FileScreenViewModel) {

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()

    val activeTransfer by viewModel.activeTransfer.collectAsState()
    val previewState by viewModel.downloadProgress.collectAsState()

    var selectedFileNode by remember { mutableStateOf<FileNode?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showFileActionBottomSheet by remember { mutableStateOf(false) }
    var showCreateFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showCreateTextFileDialog by rememberSaveable { mutableStateOf(false) }
    var showRenameDialog by rememberSaveable { mutableStateOf<FileNode?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.onEvent(FileScreenUIEvent.RefreshData) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FileScreenUIEffect.ShowSnackBar -> {
                    Log.e("SplashScreenViewModel", "LoginScreen -> effect: ${effect.message}")
                    snackbarHostState.showSnackbar(effect.message)
                }

                is FileScreenUIEffect.PreviewFile -> {
                    openFileFromCache(context = context, mimeType = effect.mimeType, file = effect.file)
                }
            }
        }
    }

    if (previewState is FileProgress.Loading) {
        DownloadProgressDialog((previewState as FileProgress.Loading).percent) {
            viewModel.onEvent(FileScreenUIEvent.CancelDownload)
        }
    }

    if (previewState is FileProgress.Success) {
        val file = (previewState as FileProgress.Success).data

        LaunchedEffect(file) {
            openFileFromCache(
                context = context,
                mimeType = viewModel.mimeType,
                file = file
            )
        }
    }

    if (previewState is FileProgress.Failure) {
        val message = (previewState as FileProgress.Failure).error
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
        }
    }

    if (showRenameDialog != null) {
        RenameDialog(
            fileId = showRenameDialog!!.id,
            fileName = showRenameDialog!!.name,
            onDismiss = { showRenameDialog = null }
        ) { fileId, newName ->
            viewModel.onEvent(FileScreenUIEvent.Rename(fileId = fileId, newName = newName))
            showRenameDialog = null
        }
    }

    if (showDeleteDialog != null) {
        DeleteDialog(
            fileId = showDeleteDialog!!,
            onDismiss = { showDeleteDialog = null }
        ) {
            viewModel.onEvent(FileScreenUIEvent.Delete(fileId = it))
            showDeleteDialog = null
        }
    }

    if (showCreateFolderDialog) {
        CreateNewFolderDialog(
            onDismiss = { showCreateFolderDialog = false }
        ) { folderName ->
            viewModel.onEvent(FileScreenUIEvent.CreateNewFolder(folderName))
            showCreateFolderDialog = false
        }
    }

    if (showCreateTextFileDialog) {
        CreateNewTextFilDialog(
            onDismiss = { showCreateTextFileDialog = false }
        ) { fileName ->
            viewModel.onEvent(FileScreenUIEvent.CreateNewFile(fileName))
            showCreateTextFileDialog = false
        }
    }

    val state = viewModel.state

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FAB(
                showActiveFileOperationFAB = activeTransfer > 0f,
                progress = activeTransfer,
                onClickActiveFileOperationFAB = {},
                showFileOperationFAB = true
            ) { showFileActionBottomSheet = true }
        },
        snackbarHost = {
            CustomSnackbarHost(modifier = modifier,snackbarHostState = snackbarHostState)
        },
        topBar = {
            FileScreenTopBar(
                title = viewModel.state.value.title.take(20),
                isRoot = viewModel.state.value.parentId == null,
                onSearchClick = { viewModel.onEvent(FileScreenUIEvent.Search) },
                onMoreClick = {},
                onBackClick = { viewModel.onEvent(FileScreenUIEvent.GoBack) }
            )
        }
    ) { paddingValues ->

        if (showFileActionBottomSheet) {
            ActionsBottomSheet(
                modifier = Modifier,
                sheetState = sheetState,
                onDismiss = { showFileActionBottomSheet = false }
            ) { fileAction ->
                when (fileAction) {
                    FileActions.CreateFolder -> showCreateFolderDialog = true
                    FileActions.UploadFile -> {}
                }
                showFileActionBottomSheet = false
            }
        }

        if (showBottomSheet && selectedFileNode != null) {
            FileScreenBottomSheet(
                sheetState = sheetState,
                fileNode = selectedFileNode!!,
                onDismiss = { showBottomSheet = false }
            ) { action ->
                when (action) {
                    Action.Rename -> {
                        selectedFileNode?.let { showRenameDialog = it }
                    }
                    Action.Move -> {
                        selectedFileNode?.let {
                            viewModel.onEvent(FileScreenUIEvent.Move(fileNode = it))
                        }
                    }
                    Action.Copy -> {
                        selectedFileNode?.let {
                            viewModel.onEvent(FileScreenUIEvent.Copy(fileNode = it))
                        }
                    }
                    Action.Delete -> {
                        selectedFileNode?.let { showDeleteDialog = it.id }
                    }
                    Action.Info -> {
                        selectedFileNode?.let { fileNode ->
                            viewModel.onEvent(FileScreenUIEvent.FileDetails(fileNode))
                        }
                    }
                    Action.Download -> {}
                }
                selectedFileNode = null
            }
        }

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

            state.value.isLoadingFiles == false && state.value.children.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.open_folder_icon),
                        contentDescription = getString(context, R.string.upload_icon),
                        modifier = Modifier.size(100.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
                    )
                    Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))
                    Text(
                        getString(context, R.string.empty_folder),
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
                        .padding(top = paddingValues.calculateTopPadding())
                        .padding(horizontal = 2.percentOfScreenWidth())
                ) {

                    item {
                        SortingAndOrderCell(
                            modifier = Modifier,
                            sortType = viewModel.state.value.sortType,
                            sortOrder = viewModel.state.value.sortOrder,
                            onSortOrderClick = {
                                viewModel.onEvent(FileScreenUIEvent.Sort(
                                    sortType = viewModel.state.value.sortType,
                                    sortOrder = if (viewModel.state.value.sortOrder == SortOrder.ASC) SortOrder.DESC
                                        else SortOrder.ASC
                                ))
                            }
                        ) {
                            viewModel.onEvent(FileScreenUIEvent.Sort(
                                sortType = it,
                                sortOrder = viewModel.state.value.sortOrder
                            ))
                        }
                    }

                    items(state.value.children) {
                        FileNodeCell(fileNode = it, onMoreClick = {
                            showBottomSheet = true
                            selectedFileNode = it
                        }) { viewModel.onEvent(FileScreenUIEvent.OpenFileNode(it)) }
                    }

                    item { Box(modifier = Modifier.padding(bottom = 1.percentOfScreenHeight())) }
                }
            }
        }

    }
}
