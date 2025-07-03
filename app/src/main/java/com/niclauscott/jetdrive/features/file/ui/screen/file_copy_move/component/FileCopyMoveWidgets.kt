package com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.component

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.util.getFileIcon
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.TextButton

@Composable
fun FileCopyMoveNodeCell(
    modifier: Modifier = Modifier,
    fileNode: FileNode,
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
                Text(fileNode.name.take(35), style = MaterialTheme.typography.bodyMedium)
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
fun CreateNewFolderDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onCreateClick: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current
    var name by remember { mutableStateOf("") }

    focusRequester.requestFocus()
    keyboardController?.show()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(
                    vertical = 2.percentOfScreenHeight(),
                    horizontal = 3.percentOfScreenWidth()
                ),
            verticalArrangement = Arrangement.spacedBy(2.percentOfScreenHeight()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                getString(context, R.string.create),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = name,
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = { name = it },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                )
            )

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    label = getString(context, R.string.cancel),
                    enabled = true,
                    onClick = onDismiss
                )

                TextButton(
                    label = getString(context, R.string.create),
                    enabled = name.isNotBlank(),
                    onClick = { onCreateClick(name) }
                )
            }
        }
    }
}

@Composable
fun CreateNewTextFilDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onCreateClick: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current
    val text = ".txt"
    val textFieldValue = remember {
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(0)
            )
        )
    }

    focusRequester.requestFocus()
    keyboardController?.show()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(
                    vertical = 2.percentOfScreenHeight(),
                    horizontal = 3.percentOfScreenWidth()
                ),
            verticalArrangement = Arrangement.spacedBy(2.percentOfScreenHeight()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                getString(context, R.string.create),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = textFieldValue.value,
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = { textFieldValue.value = it },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                )
            )

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    label = getString(context, R.string.cancel),
                    enabled = true,
                    onClick = onDismiss
                )

                TextButton(
                    label = getString(context, R.string.create),
                    enabled = textFieldValue.value.text.isNotBlank(),
                    onClick = { onCreateClick(textFieldValue.value.text) }
                )
            }
        }
    }
}

