package com.niclauscott.jetdrive.features.profile.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getString
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.component.CustomSnackbarHost
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.TextButton
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEffect
import com.niclauscott.jetdrive.features.home.ui.component.StatsCard
import com.niclauscott.jetdrive.features.profile.domain.model.User
import com.niclauscott.jetdrive.features.profile.ui.state.ProfileScreenUIEffect
import com.niclauscott.jetdrive.features.profile.ui.state.ProfileScreenUiEvent

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileScreenViewModel
) {

    val state = viewModel.state
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showEditProfileNameDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileScreenUIEffect.ShowSnackBar -> {
                    Log.e("SplashScreenViewModel", "LoginScreen -> effect: ${effect.message}")
                    snackbarHostState.showSnackbar(effect.message)
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

    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(modifier = modifier,snackbarHostState = snackbarHostState)
        }
    ) { paddingValues -> paddingValues.calculateTopPadding()
        Column(modifier = modifier.fillMaxSize()) {
            Row(modifier = modifier
                .fillMaxWidth()
                .weight(0.2f)
                .padding(horizontal = 1.percentOfScreenWidth())
            ) {
                when {
                    state.value.isProfileLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        return@Column
                    }
                    !state.value.isProfileLoading && state.value.profileData == null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                viewModel.state.value.statsError ?: getString(context, R.string.unknown_error),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        }
                        return@Scaffold
                    }
                    else -> {
                        state.value.profileData?.let {
                            ProfileCell(
                                user = it,
                                updatingProfile = state.value.isProfileUpdating,
                                onEditClick = { showEditProfileNameDialog = true }
                            )
                        }
                    }
                }
            }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .weight(0.8f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                when {
                    state.value.isStatsLoading -> {
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

                    !state.value.isStatsLoading && state.value.statsData == null ->  {
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
                                viewModel.state.value.statsError ?: getString(context, R.string.unknown_error),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        }
                        return@Scaffold
                    }

                    else -> {
                        state.value.statsData?.let { StatsCard(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCell(
    modifier: Modifier = Modifier,
    user: User, updatingProfile: Boolean,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.percentOfScreenWidth(),
                vertical = 3.percentOfScreenHeight()
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.percentOfScreenWidth())
    ) {
        // Profile Image Section
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            val imageState = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(user.picUrl)
                    .size(Size.ORIGINAL)
                    .crossfade(true)
                    .build()
            ).state

            when (imageState) {
                is AsyncImagePainter.State.Loading -> {
                    // Loading state
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is AsyncImagePainter.State.Error -> {
                    // Error state with fallback
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = getString(context, R.string.profile_picture),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                is AsyncImagePainter.State.Success -> {
                    // Success state
                    Image(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            ),
                        painter = imageState.painter,
                        contentDescription = getString(context, R.string.profile_picture),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    // Default state
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        // User Info Section
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 1.percentOfScreenHeight()),
            verticalArrangement = Arrangement.spacedBy(1.percentOfScreenHeight())
        ) {
            // Name and Edit Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User Name
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Edit Button
                IconButton(
                    onClick = onEditClick,
                    enabled = !updatingProfile,
                    modifier = Modifier.size(40.dp)
                ) {
                    if (!updatingProfile) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = getString(context, R.string.edit_profile),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                }
            }

            // Email
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )


        }
    }
}

@Composable
fun RenameProfileNameDialog(
    modifier: Modifier = Modifier,
    firstName: String, onFirstNameTextChange: (String) -> Unit,
    lastName: String, onLastNameTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onRenameClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    LocalContext.current


    var isLoading by remember { mutableStateOf(false) }

    // Enhanced validation
    val isValidFirstName = firstName.trim().isNotEmpty() &&
            firstName.trim().length <= 255

    val isValidLastName = lastName.trim().isNotEmpty() &&
            lastName.trim().length <= 255

    // Request focus when dialog opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Dialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.update),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (!isLoading) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = firstName,
                        onValueChange = { newValue ->
                            // Prevent certain characters in file names
                            val filteredValue = newValue.filter { char ->
                                char !in "\\/:*?\"<>|"
                            }
                            onFirstNameTextChange(filteredValue)
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        label = {
                            Text(
                                text = stringResource(R.string.first_name),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        supportingText = {
                            Text(
                                text = "${firstName.length}/255",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (firstName.length > 255) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        },
                        isError = firstName.trim().isEmpty() || firstName.length > 255,
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = lastName,
                        onValueChange = { newValue ->
                            // Prevent certain characters in file names
                            val filteredValue = newValue.filter { char ->
                                char !in "\\/:*?\"<>|"
                            }
                            onLastNameTextChange(filteredValue)
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        label = {
                            Text(
                                text = stringResource(R.string.last_name),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        supportingText = {
                            Text(
                                text = "${lastName.length}/255",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (lastName.length > 255) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        },
                        isError = lastName.trim().isEmpty() || lastName.length > 255,
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isValidFirstName && isValidLastName && !isLoading) {
                                    keyboardController?.hide()
                                    isLoading = true
                                    onRenameClick()
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        label = "Cancel",
                        onClick = onDismiss,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            isLoading = true
                            onRenameClick()
                        },
                        enabled = isValidFirstName && isValidLastName && !isLoading,
                        modifier = Modifier.height(40.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = if (isLoading) {
                                stringResource(R.string.renaming)
                            } else {
                                stringResource(R.string.update)
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
