package com.niclauscott.jetdrive.features.file.ui.screen.file_list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.cornerRadius
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.util.getFileIcon
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.ActionType
import com.niclauscott.jetdrive.features.landing.ui.state.FileActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileScreenBottomSheet(
    modifier: Modifier = Modifier,
    fileNode: FileNode,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onActionClicked: (Action) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.fillMaxWidth(),
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = cornerRadius(), topEnd = cornerRadius()),
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.inverseOnSurface) },
        scrimColor = Color.Black.copy(alpha = 0.9f)
    ) {

        BottomSheetHeaderCell(modifier = Modifier, fileNode = fileNode)

        Spacer(modifier = Modifier.height(0.5.percentOfScreenHeight()))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 3.percentOfScreenWidth()),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            thickness = 0.5.dp
        )

        Action.entries.filter { it.actionType == ActionType.OtherAction }.forEach { action ->
            ActionCell(action = action) { onActionClicked(action) }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.percentOfScreenWidth()),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            thickness = 0.5.dp
        )
        Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))

        Action.entries.filter { it.actionType == ActionType.ModifyingAction }.forEach { action ->
            ActionCell(action = action) { onActionClicked(action) }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth().padding(start = 4.percentOfScreenWidth()),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            thickness = 0.5.dp
        )
        Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))

        Action.entries.filter { it.actionType == ActionType.DangerousAction }.forEach { action ->
            ActionCell(action = action) { onActionClicked(action) }
        }

        Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))
    }
}

@Composable
fun BottomSheetHeaderCell(
    modifier: Modifier = Modifier,
    fileNode: FileNode,
) {
    val context = LocalContext.current
    val fileIcon by remember { mutableIntStateOf(getFileIcon(fileNode.mimeType ?: "-1")) }
    val isFolder = fileNode.type == FileNode.Companion.FileType.Folder

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(10.percentOfScreenHeight()),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // File icon with background
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isFolder)
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(fileIcon),
                        contentDescription = getString(context, R.string.file_icon),
                        tint = if (isFolder)
                            MaterialTheme.colorScheme.onTertiaryContainer
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // File information
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // File name
                Text(
                    text = fileNode.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // File details row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // File size for files only
                    if (!isFolder) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = fileNode.fileSize,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp
                                )
                            )
                        }
                    }

                    // Date with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = fileNode.updatedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCell(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    action: Action,
    onClick: (Action) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Dynamic colors based on action type
    val iconColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        action.actionType == ActionType.DangerousAction -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    val textColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        action.actionType == ActionType.DangerousAction -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    val backgroundColor = when {
        !enabled -> Color.Transparent
        isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = MaterialTheme.colorScheme.primary),
                enabled = enabled,
                ) { onClick(action) },
        color = backgroundColor,
        tonalElevation = if (isPressed) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 4.percentOfScreenWidth(),
                    vertical = 1.percentOfScreenHeight()
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.percentOfScreenWidth())
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(action.icon),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 1.percentOfScreenWidth()),
                verticalArrangement = Arrangement.spacedBy(0.5.percentOfScreenHeight())
            ) {
                // Primary text
                Text(
                    text = action.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )


            }
        }
    }
}

