package com.niclauscott.jetdrive.features.profile.ui

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.features.profile.ui.component.ProfileScreenLandscape
import com.niclauscott.jetdrive.features.profile.ui.component.ProfileScreenPortrait
import com.niclauscott.jetdrive.features.profile.ui.component.RenameProfileNameDialog
import com.niclauscott.jetdrive.features.profile.ui.state.ProfileScreenUIEffect
import com.niclauscott.jetdrive.features.profile.ui.state.ProfileScreenUiEvent
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier, viewModel: ProfileScreenViewModel) {

    val state = viewModel.state
    val context = LocalContext.current
    var showEditProfileNameDialog by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<Toast?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileScreenUIEffect.ShowSnackBar -> {
                    toast?.cancel()
                    toast = Toast.makeText(context, effect.message, Toast.LENGTH_SHORT)
                    toast?.show()
                }
            }
        }
    }

    if (showEditProfileNameDialog) {
        var firstName by remember { mutableStateOf(state.value.profileData?.firstName ?: "") }
        var lastName by remember { mutableStateOf(state.value.profileData?.lastName ?: "") }
        RenameProfileNameDialog(
            modifier = Modifier,
            firstName = firstName,
            onFirstNameTextChange = { firstName = it },
            lastName = lastName,
            onLastNameTextChange = { lastName = it },
            onDismiss = { showEditProfileNameDialog = false },
            onRenameClick = {
                viewModel.onEvent(ProfileScreenUiEvent.EditProfileName(firstName, lastName))
                showEditProfileNameDialog = false
            }
        )
    }

    Scaffold { paddingValues -> paddingValues.calculateTopPadding()
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
        when (deviceConfiguration) {
            DeviceConfiguration.MOBILE_PORTRAIT -> {
                ProfileScreenPortrait(
                    modifier = modifier.fillMaxSize(),
                    state = state.value,
                    onPhotoEdit = { viewModel.onEvent(ProfileScreenUiEvent.UploadProfilePicture(it)) },
                    onLogoutClick = {
                      viewModel.onEvent(ProfileScreenUiEvent.Logout)
                    },
                    onEditClick = { showEditProfileNameDialog = true }
                )
            }
            else -> {
                ProfileScreenLandscape(
                    modifier = modifier.fillMaxSize(),
                    state = state.value,
                    onPhotoEdit = { viewModel.onEvent(ProfileScreenUiEvent.UploadProfilePicture(it)) },
                    onLogoutClick = {
                        viewModel.onEvent(ProfileScreenUiEvent.Logout)
                    },
                    onEditClick = { showEditProfileNameDialog = true }
                )
            }
        }

    }
}

