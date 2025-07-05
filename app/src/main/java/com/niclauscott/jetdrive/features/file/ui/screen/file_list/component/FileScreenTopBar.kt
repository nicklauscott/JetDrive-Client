package com.niclauscott.jetdrive.features.file.ui.screen.file_list.component

import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun FileScreenTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    isRoot: Boolean = false,
    onMoreClick: () -> Unit = { },
    onSearchClick: () -> Unit = { },
    onBackClick: () -> Unit = { }
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
                .padding(vertical = 1.percentOfScreenHeight(),),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(0.7f), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(if (!isRoot) 0.2f else 0.1f), contentAlignment = Alignment.Center) {
                    if (!isRoot) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(R.drawable.back_icon),
                                contentDescription = getString(context, R.string.back_icon),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(if (!isRoot) 0.8f else 0.9f), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Row(
                modifier = Modifier.weight(0.3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        painter = painterResource(R.drawable.search_icon),
                        contentDescription = getString(context, R.string.create_folder_icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
                IconButton(onClick = onMoreClick) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert_icon),
                        contentDescription = getString(context, R.string.create_folder_icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }

}