package com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.FileState

@Composable
fun CloudImageViewer(
    modifier: Modifier = Modifier,
    loadingUrl: Boolean,
    imageUrl: String?,
    onStatus: (FileState) -> Unit,
    onClickImage: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)



    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            loadingUrl -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(45.dp))
                }
            }

            !loadingUrl && imageUrl == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.ImageNotSupported,
                        contentDescription = null, modifier = Modifier.size(45.dp)
                    )
                }
            }

            else -> {
                when (deviceConfiguration) {
                    DeviceConfiguration.MOBILE_PORTRAIT -> {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = onClickImage
                                ),
                            loading = {
                                onStatus(FileState.Loading)
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(45.dp))
                                }
                            },
                            error = {
                                onStatus(FileState.Failed)
                                Icon(Icons.Default.BrokenImage, contentDescription = null,
                                    tint = Color.Gray, modifier = Modifier.size(45.dp)
                                )
                            },
                            onSuccess = { onStatus(FileState.Successful) }
                        )
                    }
                    else -> {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = onClickImage
                                ),
                            loading = {
                                onStatus(FileState.Loading)
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(45.dp))
                                }
                            },
                            error = {
                                onStatus(FileState.Failed)
                                Icon(Icons.Default.BrokenImage, contentDescription = null,
                                    tint = Color.Gray, modifier = Modifier.size(45.dp)
                                )
                            },
                            onSuccess = { onStatus(FileState.Successful) }
                        )
                    }
                }
            }
        }
    }
}
