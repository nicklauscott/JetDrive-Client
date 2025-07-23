package com.niclauscott.jetdrive.features.file.ui.screen.file_search

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.domain.util.openFileFromCache
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.constant.FileProgress
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DownloadProgressDialog
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.component.FileSearchTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.state.FileSearchScreenUiEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.state.FileSearchUiEffect
import com.niclauscott.jetdrive.features.home.ui.component.HomeFileNodeCellCard
import kotlinx.coroutines.delay

@Composable
fun FileSearchScreen(modifier: Modifier = Modifier, viewModel: FileSearchScreenViewModel) {

    val state = viewModel.state
    val context = LocalContext.current
    var searchText by rememberSaveable { mutableStateOf("") }
    val previewState by viewModel.downloadProgress.collectAsState()
    var toast by remember { mutableStateOf<Toast?>(null) }

    LaunchedEffect(searchText) {
        delay(200)
        if (searchText.isNotBlank()) viewModel.onEvent(FileSearchScreenUiEvent.Search(searchText))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FileSearchUiEffect.ShowSnackBar -> {
                    toast?.cancel()
                    toast = Toast.makeText(context, effect.message, Toast.LENGTH_SHORT)
                    toast?.show()
                }

                is FileSearchUiEffect.PreviewFile -> {
                    openFileFromCache(context = context, mimeType = effect.mimeType, file = effect.file)
                }
            }
        }
    }

    if (previewState is FileProgress.Loading) {
        DownloadProgressDialog((previewState as FileProgress.Loading).percent) {
            viewModel.onEvent(FileSearchScreenUiEvent.CancelDownload)
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
            toast?.cancel()
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            FileSearchTopBar(
                searchText = searchText,
                onTextClear = {
                    if (searchText.isNotBlank()) searchText = ""
                    else viewModel.onEvent(FileSearchScreenUiEvent.GoBack)
                }
            ) { query -> searchText = query }
        },
    ) { paddingValues ->

        when {
            state.value.isSearching -> {
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

            else -> {
                val fileNodes = state.value.fileNodes

                if (fileNodes.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (searchText.isNotBlank()) {
                            Text(
                                getString(context, R.string.no_result),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        }

                    }
                    return@Scaffold
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 2.percentOfScreenWidth())
                        .consumeWindowInsets(WindowInsets.navigationBars)
                ) {
                    items(fileNodes) {
                        HomeFileNodeCellCard(fileNode = it) {
                            viewModel.onEvent(FileSearchScreenUiEvent.OpenFileNode(it))
                        }
                    }
                }

            }

        }

    }

}