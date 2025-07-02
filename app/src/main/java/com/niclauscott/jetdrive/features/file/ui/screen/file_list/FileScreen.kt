package com.niclauscott.jetdrive.features.file.ui.screen.file_list

import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.util.TAG
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DeleteDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileNodeCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileScreenTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.JetDriveModalBottomSheet
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.RenameDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.SortingAndOrderCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileScreen(modifier: Modifier = Modifier, viewModel: FileScreenViewModel) {

    val context = LocalContext.current

    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedFileNode by remember { mutableStateOf<FileNode?>(null) }

    val sheetState = rememberModalBottomSheetState()

    var showRenameDialog by rememberSaveable { mutableStateOf<FileNode?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.onEvent(FileScreenUIEvent.RefreshData) }

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

    val state = viewModel.state

    Scaffold(
        modifier = modifier,
        topBar = {
            FileScreenTopBar(
                title = viewModel.state.value.title.take(20),
                isSearch = isSearching,
                isRoot = viewModel.state.value.parentId == null,
                onSearchClick = { isSearching = true },
                onDone = {
                    keyboardController?.hide();
                    searchText = ""
                },
                searchText = searchText,
                onCancelSearch = {
                    isSearching = false;
                    searchText = ""
                    // Clear search result

                },
                onSearchTextChange = { newText ->
                    searchText = newText
                    // Perform search
                },
                onBackClick = {
                    viewModel.onEvent(FileScreenUIEvent.GoBack)
                }
            )
        }
    ) { paddingValues ->

        if (showBottomSheet && selectedFileNode != null) {
            JetDriveModalBottomSheet(
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
                        .padding(paddingValues)
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
                }
            }
        }

    }
}

