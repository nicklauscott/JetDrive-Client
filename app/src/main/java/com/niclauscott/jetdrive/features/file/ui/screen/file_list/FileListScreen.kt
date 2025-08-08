package com.niclauscott.jetdrive.features.file.ui.screen.file_list

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRowDefaults.Indicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.domain.util.openFileFromCache
import com.niclauscott.jetdrive.core.ui.component.TransferServicePermissionHandler
import com.niclauscott.jetdrive.core.ui.component.hasTransferServicePermission
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.constant.FileProgress
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.CreateNewFolderDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.CreateNewTextFilDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DeleteDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DownloadProgressDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileNodeCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileScreenBottomSheet
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileScreenTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.MyCustomIndicator
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.RenameDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.SortingAndOrderCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortOrder
import com.niclauscott.jetdrive.features.landing.ui.components.ActionsBottomSheet
import com.niclauscott.jetdrive.features.landing.ui.components.FAB
import com.niclauscott.jetdrive.features.landing.ui.state.FileActions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(modifier: Modifier = Modifier, viewModel: FileScreenViewModel) {

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    val activeTransfer by viewModel.activeTransfer.collectAsState()
    val previewState by viewModel.downloadProgress.collectAsState()

    var requestPermission by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    var selectedFileNode by remember { mutableStateOf<FileNode?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showFileActionBottomSheet by remember { mutableStateOf(false) }
    var showCreateFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showCreateTextFileDialog by rememberSaveable { mutableStateOf(false) }
    var showRenameDialog by rememberSaveable { mutableStateOf<FileNode?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var toast by remember { mutableStateOf<Toast?>(null) }
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) { permissionGranted = hasTransferServicePermission(context) }
    LaunchedEffect(Unit) { viewModel.onEvent(FileScreenUIEvent.RefreshOnAppear) }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FileScreenUIEffect.ShowSnackBar -> {
                    toast?.cancel()
                    toast = Toast.makeText(context, effect.message, Toast.LENGTH_SHORT)
                    toast?.show()
                }
                is FileScreenUIEffect.PreviewFile -> {
                    openFileFromCache(context = context, mimeType = effect.mimeType, file = effect.file)
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
                viewModel.onEvent(FileScreenUIEvent.UploadFile(uri.toString()))
            } catch (e: SecurityException) {
                Log.e(TAG("HomeScreen"), "Unable to persist URI permission: ${e.message}")
            }
        }
    }

    if (requestPermission) {
        TransferServicePermissionHandler(
            onPermissionGranted = {
                requestPermission = false
                permissionGranted = true
            },
            onPermissionDenied = {
                requestPermission = false
                permissionGranted = false
                toast?.cancel()
                toast = Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT)
                toast?.show()
            }
        )
    }

    if (previewState is FileProgress.Loading) {
        DownloadProgressDialog((previewState as FileProgress.Loading).percent) {
            viewModel.onEvent(FileScreenUIEvent.CancelDownload)
        }
    }

    if (previewState is FileProgress.Success) {
        val file = (previewState as FileProgress.Success).data
        LaunchedEffect(file) {
            openFileFromCache(context = context, mimeType = viewModel.mimeType, file = file)
            viewModel.onEvent(FileScreenUIEvent.CancelDownload)
        }
    }

    if (previewState is FileProgress.Failure) {
        val message = (previewState as FileProgress.Failure).error
        LaunchedEffect(message) {
            toast?.cancel()
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast?.show()
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
                showActiveFileOperationFAB = activeTransfer != null,
                progress = activeTransfer ?: 0f,
                onClickActiveFileOperationFAB = { viewModel.onEvent(FileScreenUIEvent.OpenTransferScreen) },
                showFileOperationFAB = true
            ) { showFileActionBottomSheet = true }
        },
        topBar = {
            FileScreenTopBar(
                title = viewModel.state.value.title.take(20),
                isRoot = viewModel.state.value.parentId == null,
                onSearchClick = { viewModel.onEvent(FileScreenUIEvent.Search) },
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
                    FileActions.UploadFile -> {
                        if (permissionGranted) {
                            launcher.launch(arrayOf("*/*"))
                        } else requestPermission = true
                    }
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
                    Action.Rename -> selectedFileNode?.let { showRenameDialog = it }
                    Action.Delete ->  selectedFileNode?.let { showDeleteDialog = it.id }
                    Action.Info -> selectedFileNode?.let { viewModel.onEvent(FileScreenUIEvent.FileDetails(it)) }
                    Action.Download -> {
                        if (permissionGranted) {
                            selectedFileNode?.let { viewModel.onEvent(FileScreenUIEvent.Download(it)) }
                        } else requestPermission = true
                    }
                    Action.Move ->  selectedFileNode?.let { viewModel.onEvent(FileScreenUIEvent.Move(fileNode = it)) }
                    Action.Copy -> selectedFileNode?.let { viewModel.onEvent(FileScreenUIEvent.Copy(fileNode = it)) }
                }
                selectedFileNode = null
            }
        }

        PullToRefreshBox(
            modifier = modifier
                .padding(top = paddingValues.calculateTopPadding()),
            isRefreshing = viewModel.state.value.isRefreshing,
            onRefresh = {
                if (!viewModel.state.value.isLoadingFiles) viewModel.onEvent(FileScreenUIEvent.RefreshData)
            },
            state = pullRefreshState,
            indicator = {
                MyCustomIndicator(
                    enabled = !viewModel.state.value.isLoadingFiles,
                    state = pullRefreshState,
                    isRefreshing = viewModel.state.value.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            if (state.value.isLoadingFiles) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
            else if (!state.value.isLoadingFiles && state.value.errorMessage != null) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
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
                    }
                }
            }
            else if (!state.value.isLoadingFiles && state.value.children.isEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
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
                    }
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
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
