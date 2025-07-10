package com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight

@Composable
fun PortraitFilePreviewScreenTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    onBackClick: () -> Unit
) {

    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth()
            .height(8.percentOfScreenHeight()),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 1.percentOfScreenHeight()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(0.2f), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = getString(context, R.string.back_icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(0.8f), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}

@Composable
fun LandscapeFilePreviewScreenTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    onBackClick: () -> Unit
) {

    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth()
            .height(12.percentOfScreenHeight()),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 1.percentOfScreenHeight()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(0.2f).fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = getString(context, R.string.back_icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(0.8f).fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}