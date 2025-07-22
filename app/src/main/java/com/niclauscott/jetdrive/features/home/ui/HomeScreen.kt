package com.niclauscott.jetdrive.features.home.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.CreateNewFolderDialog
import com.niclauscott.jetdrive.features.home.ui.component.HomeScreenLandscape
import com.niclauscott.jetdrive.features.home.ui.component.HomeScreenPortrait
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUIEvent
import com.niclauscott.jetdrive.features.landing.ui.components.ActionsBottomSheet
import com.niclauscott.jetdrive.features.landing.ui.components.FAB
import com.niclauscott.jetdrive.features.landing.ui.state.FileActions
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeScreenViewModel) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var showCreateFolderDialog by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val activeTransfer by viewModel.activeTransfer.collectAsState()
    val state = viewModel.state
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
                viewModel.onEvent(HomeScreenUIEvent.UploadFile(uri.toString()))
            } catch (e: SecurityException) {
                Log.e(TAG("HomeScreen"), "Unable to persist URI permission: ${e.message}")
            }
        }
    }


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
        contentWindowInsets = WindowInsets.statusBars,
        floatingActionButton = {
            FAB(
                showActiveFileOperationFAB = activeTransfer != null,
                progress = activeTransfer ?: 0f,
                onClickActiveFileOperationFAB = { viewModel.onEvent(HomeScreenUIEvent.OpenTransferScreen) },
                showFileOperationFAB = true
            ) { showBottomSheet = true }
        },
    ) { paddingValues ->
        val innerModifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.navigationBars)

        if (showBottomSheet) {
            ActionsBottomSheet(
                modifier = Modifier,
                sheetState = sheetState,
                onDismiss = { showBottomSheet = false }
            ) { fileAction ->
                when (fileAction) {
                    FileActions.CreateFolder -> showCreateFolderDialog = true
                    FileActions.UploadFile -> launcher.launch(arrayOf("*/*"))
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

            !state.value.isLoading && state.value.data == null ->  {
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

                val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
                val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

                when (deviceConfiguration) {
                    DeviceConfiguration.MOBILE_PORTRAIT -> {
                        HomeScreenPortrait(
                            modifier = innerModifier, state = state.value, paddingValues = paddingValues
                        ) {
                            viewModel.onEvent(HomeScreenUIEvent.OpenFileNode(it))
                        }
                    }
                    else -> {
                        HomeScreenLandscape(
                            modifier = innerModifier, state = state.value, paddingValues = paddingValues
                        ) {
                            viewModel.onEvent(HomeScreenUIEvent.OpenFileNode(it))
                        }

                    }
                }

            }
        }

    }
}


