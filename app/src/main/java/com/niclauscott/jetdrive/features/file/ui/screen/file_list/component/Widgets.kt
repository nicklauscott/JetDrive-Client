package com.niclauscott.jetdrive.features.file.ui.screen.file_list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.util.getFileIcon


@Composable
fun FileNodeCell(
    modifier: Modifier = Modifier,
    fileNode: FileNode,
    onMoreClick: () -> Unit,
    onClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val fileIcon by remember { mutableIntStateOf(getFileIcon(fileNode.mimeType ?: "-1")) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 2.percentOfScreenWidth())
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick(fileNode.id, fileNode.name) }
                .padding(1.percentOfScreenHeight()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
                Icon(
                    painter = painterResource(fileIcon),
                    contentDescription = getString(context, R.string.file_icon),
                    tint = if (fileNode.type == FileNode.Companion.FileType.Folder) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(35.dp)
                )
            }
            Row(modifier = Modifier.weight(0.85f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier
                    .weight(0.8f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(fileNode.name.take(35), style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        if (fileNode.type == FileNode.Companion.FileType.File) {
                            Text(
                                "${fileNode.fileSize } • ", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        }
                        Text(
                            fileNode.updatedDate, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                }

                Box(
                    modifier = Modifier.weight(0.2f),
                    contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = onMoreClick) {
                        Icon(
                            painter = painterResource(R.drawable.more_vert_icon),
                            contentDescription = getString(context, R.string.more_option),
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                }
            }
        }

        Row(modifier = modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(0.15f))
            Box(
                modifier = Modifier.weight(0.85f),
                contentAlignment = Alignment.TopStart
            ) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    thickness = 0.5.dp
                )
            }
        }
    }

}

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    label: String,
    texStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(
                vertical = 1.percentOfScreenHeight(),
                horizontal = 2.percentOfScreenWidth()
            )
    ) {
        Text(
            label,
            style = texStyle, //MaterialTheme.typography.bodyMedium,
            color = if (enabled) MaterialTheme.colorScheme.inverseOnSurface
            else MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean,
    texStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(
                vertical = 1.percentOfScreenHeight(),
                horizontal = 2.percentOfScreenWidth()
            )
    ) {
        Text(
            label,
            style = texStyle,
            color = textColor
        )
    }
}