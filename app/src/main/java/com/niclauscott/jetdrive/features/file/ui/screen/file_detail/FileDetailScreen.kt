package com.niclauscott.jetdrive.features.file.ui.screen.file_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.util.getFileIcon
import com.niclauscott.jetdrive.features.file.ui.component.CustomDropDownMenu
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component.DetailCell
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component.FileDetailTopBar
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.ActionType
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel

@Composable
fun FileDetailScreen(
    modifier: Modifier = Modifier,
    fileNode: FileNode,
    landingScreenViewModel: LandingScreenViewModel,
    onBackClick: () -> Unit
) {
    val fileIcon by remember { mutableIntStateOf(getFileIcon(fileNode.mimeType ?: "-1")) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        landingScreenViewModel.hideBottomBars()
        landingScreenViewModel.hideFab()
        onDispose {
            landingScreenViewModel.showBottomBars()
            landingScreenViewModel.showFab()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FileDetailTopBar(
                onDownloadClick = {},
                onActionClicked = {},
                onBackClick = onBackClick
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 3.percentOfScreenWidth())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(fileIcon),
                    contentDescription = getString(context, R.string.file_icon),
                    tint = if (fileNode.type == FileNode.Companion.FileType.Folder) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.width(1.percentOfScreenWidth()))

                Text(
                    text = fileNode.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.width(1.percentOfScreenHeight()))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                thickness = 0.5.dp
            )

            DetailCell(label = "Size", value = fileNode.fileSize)
            DetailCell(label = "Added", value = fileNode.createdDate)
            DetailCell(label = "Last Modified", value = fileNode.updatedDate)
        }
    }
}
