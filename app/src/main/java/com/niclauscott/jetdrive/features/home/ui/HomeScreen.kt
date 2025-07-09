package com.niclauscott.jetdrive.features.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.component.CreateNewFolderDialog
import com.niclauscott.jetdrive.features.home.ui.component.HomeFileNodeCellCard
import com.niclauscott.jetdrive.features.home.ui.component.MinimalStatsCard
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUIEvent
import com.niclauscott.jetdrive.features.landing.ui.components.ActionsBottomSheet
import com.niclauscott.jetdrive.features.landing.ui.components.FAB
import com.niclauscott.jetdrive.features.landing.ui.state.FileActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeScreenViewModel) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var showCreateFolderDialog by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val activeTransfer by viewModel.activeTransfer.collectAsState()
    val state = viewModel.state
    val context = LocalContext.current

    if (showCreateFolderDialog) {
        CreateNewFolderDialog(
            onDismiss = { showCreateFolderDialog = false }
        ) { folderName ->
            viewModel.onEvent(HomeScreenUIEvent.CreateNewFolder(folderName))
            showCreateFolderDialog = false
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        floatingActionButton = {
            FAB(
                showActiveFileOperationFAB = activeTransfer > 0f,
                progress = activeTransfer,
                onClickActiveFileOperationFAB = {},
                showFileOperationFAB = true
            ) { showBottomSheet = true }
        },
    ) { paddingValues ->
        val innerModifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)

        if (showBottomSheet) {
            ActionsBottomSheet(
                modifier = Modifier,
                sheetState = sheetState,
                onDismiss = { showBottomSheet = false }
            ) { fileAction ->
                when (fileAction) {
                    FileActions.CreateFolder -> showCreateFolderDialog = true
                    FileActions.UploadFile -> {}
                }
                showBottomSheet = false
            }
        }

        when {
            state.value.isLoading -> {
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

            !state.value.isLoading && state.value.error != null ->  {
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
                        viewModel.state.value.error ?: getString(context, R.string.unknown_error),
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
                        Spacer(modifier = Modifier.height(16.dp))
                        state.value.data?.let { MinimalStatsCard(stats = it) }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = getString(context, R.string.recent_files),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val recentFiles = state.value.data?.recentFiles ?: emptyList()
                    items(recentFiles) {
                        HomeFileNodeCellCard(fileNode = it) {

                        }
                    }

                    item { Box(modifier = Modifier.padding(bottom = 1.percentOfScreenHeight())) }

                }
            }

        }

    }
}
