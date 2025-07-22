package com.niclauscott.jetdrive.features.profile.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.home.ui.component.StatsCard
import com.niclauscott.jetdrive.features.profile.ui.state.ProfileScreenUiState


@Composable
fun ProfileScreenLandscape(
    modifier: Modifier = Modifier,
    state: ProfileScreenUiState,
    onPhotoEdit: (String) -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Row(modifier = modifier.fillMaxSize()) {
        Row(modifier = modifier
            .fillMaxHeight()
            .weight(0.3f)
            .padding(horizontal = 1.percentOfScreenWidth()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                state.isProfileLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    return
                }
                !state.isProfileLoading && state.profileData == null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            state.statsError ?: getString(context, R.string.unknown_error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                    return
                }
                else -> {
                    state.profileData?.let {
                        ProfileCellLandscape(
                            user = it,
                            updatingProfile = state.isProfileUpdating,
                            imageUploading = state.isProfilePictureUpdating,
                            onPhotoEdit = onPhotoEdit,
                            onEditClick = onEditClick
                        )
                    }
                }
            }
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .weight(0.7f)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            when {
                state.isStatsLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    return
                }

                !state.isStatsLoading && state.statsData == null ->  {
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
                        Text(state.statsError ?: getString(context, R.string.unknown_error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                    return
                }

                else -> {
                    state.statsData?.let { StatsCard(it) }
                }
            }
        }
    }
}