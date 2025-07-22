package com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.ui.component.CustomDropDownMenu
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.ActionType

@Composable
fun FileDetailTopBar(
    modifier: Modifier = Modifier,
    onDownloadClick: () -> Unit,
    onActionClicked: (Action) -> Unit,
    onBackClick: () -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    when (deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            FileDetailTopBarPortrait(
                modifier = modifier,
                onDownloadClick = onDownloadClick,
                onActionClicked = onActionClicked,
                onBackClick = onBackClick
            )
        }
        else -> {
            FileDetailTopBarLandscape(
                modifier = modifier,
                onDownloadClick = onDownloadClick,
                onActionClicked = onActionClicked,
                onBackClick = onBackClick
            )
        }
    }

}

@Composable
fun FileDetailTopBarLandscape(
    modifier: Modifier = Modifier,
    onDownloadClick: () -> Unit,
    onActionClicked: (Action) -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var offset = Offset.Zero
    var showMoreOption by remember { mutableStateOf(false) }
    val actions = Action.entries.filter { it.actionType != ActionType.OtherAction }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 1.percentOfScreenWidth()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(0.1f), contentAlignment = Alignment.CenterStart) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.back_icon),
                    contentDescription = getString(context, R.string.back_icon),
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Box(modifier = Modifier.weight(0.7f))

        Row(
            modifier = Modifier.weight(0.2f),
            horizontalArrangement = Arrangement.End
        ) {

            Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = onDownloadClick) {
                    Icon(
                        painter = painterResource(R.drawable.download_icon),
                        contentDescription = getString(context, R.string.download_icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.CenterEnd) {
                IconButton(
                    modifier = Modifier
                        .pointerInteropFilter {
                            offset = Offset(it.x, it.y)
                            false
                        },
                    onClick = { showMoreOption = true }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert_icon),
                        contentDescription = getString(context, R.string.more_option),
                        modifier = Modifier.size(30.dp)
                    )
                }

                CustomDropDownMenu(
                    modifier = Modifier,
                    offset = DpOffset(
                        x = with(LocalDensity.current) { offset.x.toDp()  },
                        y = with(LocalDensity.current) { offset.y.toDp()  } // Push down
                    ),
                    expanded = showMoreOption,
                    items = actions.map { it.name },
                    onDismiss = { showMoreOption = false }
                ) {
                    showMoreOption = false
                    onActionClicked(Action.valueOf(it))
                }
            }
        }
    }
}

@Composable
fun FileDetailTopBarPortrait(
    modifier: Modifier = Modifier,
    onDownloadClick: () -> Unit,
    onActionClicked: (Action) -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var offset = Offset.Zero
    var showMoreOption by remember { mutableStateOf(false) }
    val actions = Action.entries.filter { it.actionType != ActionType.OtherAction }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 1.percentOfScreenWidth()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(0.2f), contentAlignment = Alignment.Center) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.back_icon),
                    contentDescription = getString(context, R.string.back_icon),
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Box(modifier = Modifier.weight(0.6f))

        Row(
            modifier = Modifier
                .weight(0.3f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = onDownloadClick) {
                    Icon(
                        painter = painterResource(R.drawable.download_icon),
                        contentDescription = getString(context, R.string.download_icon),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                IconButton(
                    modifier = Modifier
                        .pointerInteropFilter {
                            offset = Offset(it.x, it.y)
                            false
                        },
                    onClick = { showMoreOption = true }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert_icon),
                        contentDescription = getString(context, R.string.more_option),
                        modifier = Modifier.size(30.dp)
                    )
                }

                CustomDropDownMenu(
                    modifier = Modifier,
                    offset = DpOffset(
                        x = with(LocalDensity.current) { offset.x.toDp()  },
                        y = with(LocalDensity.current) { offset.y.toDp()  } // Push down
                    ),
                    expanded = showMoreOption,
                    items = actions.map { it.name },
                    onDismiss = { showMoreOption = false }
                ) {
                    showMoreOption = false
                    onActionClicked(Action.valueOf(it))
                }
            }
        }
    }
}