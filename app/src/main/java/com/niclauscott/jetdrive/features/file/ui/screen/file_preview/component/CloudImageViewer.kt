package com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CloudImageViewer(
    modifier: Modifier = Modifier,
    loadingUrl: Boolean, imageUrl: String?
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            loadingUrl -> {
                CircularProgressIndicator()
                return@Box
            }

            !loadingUrl && imageUrl == null -> {
                Icon(
                    imageVector = Icons.Filled.ImageNotSupported,
                    contentDescription = null
                )
                return@Box
            }

            else -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}