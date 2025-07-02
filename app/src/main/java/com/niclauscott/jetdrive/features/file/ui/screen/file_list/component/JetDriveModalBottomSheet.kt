package com.niclauscott.jetdrive.features.file.ui.screen.file_list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JetDriveModalBottomSheet(
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
        val fileIcon by remember { mutableIntStateOf(getFileIcon(fileNode.mimeType ?: "-1")) }
        val context = LocalContext.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 3.percentOfScreenWidth()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(0.1f), contentAlignment = Alignment.CenterStart) {
                Icon(
                    painter = painterResource(fileIcon),
                    contentDescription = getString(context, R.string.file_icon),
                    tint = if (fileNode.type == FileNode.Companion.FileType.Folder) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(35.dp)
                )
            }

            Column(modifier = Modifier
                .weight(0.9f)
                .padding(start = 2.percentOfScreenWidth()),
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
        }

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
            ActionCell(action = action, dangerousAction = true) { onActionClicked(action) }
        }

        Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))
    }
}

@Composable
fun ActionCell(
    modifier: Modifier = Modifier,
    dangerousAction: Boolean = false,
    action: Action,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 3.percentOfScreenWidth())
            .padding(vertical = 1.percentOfScreenHeight()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(modifier = Modifier.weight(0.1f), contentAlignment = Alignment.CenterStart) {
            Icon(
                painter = painterResource(action.icon),
                contentDescription = getString(context, R.string.action_icon),
                tint = if (dangerousAction) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.size(30.dp)
            )
        }

        Box(modifier = Modifier.weight(0.9f), contentAlignment = Alignment.CenterStart) {
            Text(
                action.name, style = MaterialTheme.typography.bodyMedium,
                color = if (dangerousAction) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }
}
