package com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.util.getFileIcon

@Composable
fun FileDetailScreenPortrait(
    modifier: Modifier = Modifier, paddingValues: PaddingValues, fileNode: FileNode
) {
    val fileIcon by remember { mutableIntStateOf(getFileIcon(fileNode.mimeType ?: "-1")) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(paddingValues)
            .padding(horizontal = 4.percentOfScreenWidth())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.percentOfScreenHeight()),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon with background circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (fileNode.type == FileNode.Companion.FileType.Folder)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(fileIcon),
                        contentDescription = getString(context, R.string.file_icon),
                        tint = if (fileNode.type == FileNode.Companion.FileType.Folder)
                            MaterialTheme.colorScheme.onTertiaryContainer
                        else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = fileNode.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (fileNode.type == FileNode.Companion.FileType.Folder) "Folder" else "File",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.percentOfScreenHeight()))

        Text(
            text = getString(context, R.string.details),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ModernDetailCard(
                    icon = Icons.Default.Storage,
                    label = getString(context, R.string.size),
                    value = fileNode.fileSize,
                    iconTint = MaterialTheme.colorScheme.primary
                )
            }

            item {
                ModernDetailCard(
                    icon = Icons.Default.Add,
                    label = getString(context, R.string.added),
                    value = fileNode.createdDate,
                    iconTint = MaterialTheme.colorScheme.secondary
                )
            }

            item {
                ModernDetailCard(
                    icon = Icons.Default.Edit,
                    label = getString(context, R.string.modified),
                    value = fileNode.updatedDate,
                    iconTint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }

}
