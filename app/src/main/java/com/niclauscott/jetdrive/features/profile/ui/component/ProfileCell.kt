package com.niclauscott.jetdrive.features.profile.ui.component

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.profile.domain.model.User

@Composable
fun ProfileCellPortrait(
    modifier: Modifier = Modifier,
    user: User, updatingProfile: Boolean,
    imageUploading: Boolean,
    onPhotoEdit: (String) -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val imageUrl by remember(user) { mutableStateOf(user.picUrl) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
                onPhotoEdit(uri.toString())
            } catch (e: SecurityException) {
                Log.e(TAG("HomeScreen"), "Unable to persist URI permission: ${e.message}")
            }
        }
    }

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

            if (imageUploading) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp), strokeWidth = 1.5.dp)
                return@Box
            }

            val imageState = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .size(Size.ORIGINAL)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .crossfade(true)
                    .build()
            ).state

            when (imageState) {
                is AsyncImagePainter.State.Loading -> {
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

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = getString(context, R.string.edit_profile),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                        .clickable { launcher.launch(arrayOf("image/*")) }
                        .padding(4.dp)
                )
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
fun ProfileCellLandscape(
    modifier: Modifier = Modifier,
    user: User, updatingProfile: Boolean,
    imageUploading: Boolean,
    onPhotoEdit: (String) -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val imageUrl by remember(user) { mutableStateOf(user.picUrl) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
                onPhotoEdit(uri.toString())
            } catch (e: SecurityException) {
                Log.e(TAG("HomeScreen"), "Unable to persist URI permission: ${e.message}")
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image Section
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {

            if (imageUploading) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp), strokeWidth = 1.5.dp)
                return@Box
            }

            val imageState = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .size(Size.ORIGINAL)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .crossfade(true)
                    .build()
            ).state

            when (imageState) {
                is AsyncImagePainter.State.Loading -> {
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

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = getString(context, R.string.edit_profile),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                        .clickable { launcher.launch(arrayOf("image/*")) }
                        .padding(4.dp)
                )
            }
        }

        // User Info Section
        Column(
            modifier = Modifier
                .padding(vertical = 1.percentOfScreenHeight()),
            verticalArrangement = Arrangement.spacedBy(1.percentOfScreenHeight())
        ) {
            // Name and Edit Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
