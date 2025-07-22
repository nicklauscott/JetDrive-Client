package com.niclauscott.jetdrive.features.home.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUiState

@Composable
fun HomeScreenPortrait(
    modifier: Modifier = Modifier,
    state: HomeScreenUiState,
    paddingValues: PaddingValues,
    onFileNodeClick: (FileNode) -> Unit
) {
    val context = LocalContext.current

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            return
        }

        !state.isLoading && state.data == null ->  {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.error_icon),
                    contentDescription = getString(context, R.string.upload_icon),
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))
                Text(
                    state.error ?: getString(context, R.string.unknown_error),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
            return
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = 2.percentOfScreenWidth())
            ) {

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    state.data?.let { MinimalStatsCard(stats = it) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = getString(context, R.string.recent_files),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                val recentFiles = state.data?.recentFiles ?: emptyList()
                items(recentFiles) { HomeFileNodeCellCard(fileNode = it) { onFileNodeClick(it) } }
                item { Box(modifier = Modifier.padding(bottom = 1.percentOfScreenHeight())) }
            }
        }

    }
}