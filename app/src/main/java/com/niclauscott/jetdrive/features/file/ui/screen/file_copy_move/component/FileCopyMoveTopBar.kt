package com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth

@Composable
fun FileCopyMoveTopBar(
    modifier: Modifier = Modifier,
    isRoot: Boolean,
    title: String,
    onCreateFolderClick: () -> Unit,
    onBackClick: () -> Unit,
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
                .padding(vertical = 1.percentOfScreenHeight(), horizontal = 2.percentOfScreenWidth()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(0.2f), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(if (isRoot) R.drawable.clear_icon else R.drawable.back_icon),
                        contentDescription = getString(context, R.string.back_icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Box(modifier = Modifier.weight(0.2f), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = onCreateFolderClick) {
                    Icon(
                        painter = painterResource(R.drawable.create_folder_icon),
                        contentDescription = getString(context, R.string.create_folder_icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}
