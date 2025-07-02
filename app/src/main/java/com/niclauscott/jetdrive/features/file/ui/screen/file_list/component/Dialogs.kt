package com.niclauscott.jetdrive.features.file.ui.screen.file_list.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.core.util.TAG

@Composable
fun RenameDialog(
    modifier: Modifier = Modifier,
    fileName: String,
    fileId: String,
    onDismiss: () -> Unit,
    onRenameClick: (String, String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current
    var name by remember { mutableStateOf(fileName) }

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
                getString(context, R.string.rename),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )

            // Text
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
                    label = getString(context, R.string.rename),
                    enabled = name != fileName,
                    onClick = { onRenameClick(fileId, name) }
                )
            }
        }
    }
}


@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    fileId: String,
    onDismiss: () -> Unit,
    onDeleteClick: (String) -> Unit
) {

    val context = LocalContext.current

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
                getString(context, R.string.delete_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
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
                    label = getString(context, R.string.delete),
                    enabled = true,
                    onClick = { onDeleteClick(fileId) }
                )
            }
        }
    }
}