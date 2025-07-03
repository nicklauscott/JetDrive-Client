package com.niclauscott.jetdrive.features.file.ui.screen.file_search

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.component.FileNodeSearchResultCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.component.FileSearchTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.state.FileSearchScreenUiEvent
import kotlinx.coroutines.delay

@Composable
fun FileSearchScreen(modifier: Modifier = Modifier, viewModel: FileSearchScreenViewModel) {

    val state = viewModel.state
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(searchText) {
        delay(200)
        if (searchText.isNotBlank()) viewModel.onEvent(FileSearchScreenUiEvent.Search(searchText))
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
                        .consumeWindowInsets(WindowInsets.navigationBars)
                ) {
                    items(fileNodes) {
                        FileNodeSearchResultCell(fileNode = it) {
                            viewModel.onEvent(FileSearchScreenUiEvent.OpenFileNode(it))
                        }
                    }
                }

            }

        }

    }

}