package com.niclauscott.jetdrive.features.transfer.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth

sealed interface TransferAction {
    data object CancelAllTransfer: TransferAction
    data object ToggleAllTransfer: TransferAction
    data class ToggleSpecificTransfers(val transferType: TransferType): TransferAction
    data class CancelSpecificTransfers(val transferType: TransferType): TransferAction
}

/*
        -------------------------------------------------------------------------------
          <- Transfer                                                       ||>     :
                                                                            ------------------
                                                                           | Cancel Uploads   |
                                                                           | Cancel Downloads |
                                                                           | ||> All          |
                                                                           | Cancel All       |
                                                                            ------------------
          Downloads                                                          Uploads
        -------------------------------------------------------------------------------
 */

enum class TransferActions(val displayName: String) {
    ToggleAllTransfer(""),
    CancelUploads("Cancel Uploads"),
    CancelDownloads("Cancel Downloads"),
    CancelAllTransfer("Cancel All")
}

@Composable
fun FileTransferTopBar(
    modifier: Modifier = Modifier,
    isCurrentScreenDownload: Boolean,
    isUploadsPaused: Boolean,
    isDownloadsPaused: Boolean,
    isAllTransferPaused: Boolean,
    onTransferAction: (TransferAction) -> Unit,
    onBackClick: () -> Unit,
    onScreenToggle: () -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    when (deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            FileTransferTopBarPortrait(
                modifier = modifier,
                isCurrentScreenDownload = isCurrentScreenDownload,
                isUploadsPaused = isUploadsPaused,
                isDownloadsPaused = isDownloadsPaused,
                isAllTransferPaused = isAllTransferPaused,
                onTransferAction = onTransferAction,
                onBackClick = onBackClick,
                onScreenToggle = onScreenToggle
            )
        }
        else -> {
            FileTransferTopBarLandscape(
                modifier = modifier,
                isCurrentScreenDownload = isCurrentScreenDownload,
                isUploadsPaused = isUploadsPaused,
                isDownloadsPaused = isDownloadsPaused,
                isAllTransferPaused = isAllTransferPaused,
                onTransferAction = onTransferAction,
                onBackClick = onBackClick,
                onScreenToggle = onScreenToggle
            )
        }
    }
}


@Composable
fun FileTransferTopBarPortrait(
    modifier: Modifier = Modifier,
    isCurrentScreenDownload: Boolean,
    isUploadsPaused: Boolean,
    isDownloadsPaused: Boolean,
    isAllTransferPaused: Boolean,
    onTransferAction: (TransferAction) -> Unit,
    onBackClick: () -> Unit,
    onScreenToggle: () -> Unit
) {

    val context = LocalContext.current
    var offset = Offset.Zero
    var showMoreOption by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 1.percentOfScreenWidth()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(0.2f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.back_icon),
                            contentDescription = getString(context, R.string.back_icon),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = getString(context, R.string.transfer),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(0.3f),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        IconButton(onClick = {
                            if (isCurrentScreenDownload) {
                                onTransferAction(TransferAction.ToggleSpecificTransfers(TransferType.DOWNLOAD))
                                return@IconButton
                            }
                            onTransferAction(TransferAction.ToggleSpecificTransfers(TransferType.UPLOAD))
                        }) {
                            if (isCurrentScreenDownload) {
                                Icon(
                                    imageVector = if (isDownloadsPaused) Icons.Outlined.PlayArrow
                                    else Icons.Outlined.Pause,
                                    contentDescription = getString(context, R.string.download_icon),
                                    modifier = Modifier.size(30.dp)
                                )
                                return@IconButton
                            }

                            Icon(
                                imageVector = if (isUploadsPaused) Icons.Outlined.PlayArrow
                                else Icons.Outlined.Pause,
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

                        FileTransferDropDownMenu(
                            modifier = Modifier,
                            offset = DpOffset(
                                x = with(LocalDensity.current) { offset.x.toDp()  },
                                y = with(LocalDensity.current) { offset.y.toDp()  }
                            ),
                            isDownloadScreen = isCurrentScreenDownload,
                            expanded = showMoreOption,
                            isAllTransferPaused = isAllTransferPaused,
                            onDismiss = { showMoreOption = false }
                        ) {
                            showMoreOption = false
                            when (it) {
                                TransferActions.ToggleAllTransfer -> onTransferAction(TransferAction.ToggleAllTransfer)
                                TransferActions.CancelUploads -> {
                                    onTransferAction(TransferAction.CancelSpecificTransfers(TransferType.UPLOAD))
                                }
                                TransferActions.CancelDownloads -> {
                                    onTransferAction(TransferAction.CancelSpecificTransfers(TransferType.DOWNLOAD))
                                }
                                TransferActions.CancelAllTransfer -> onTransferAction(TransferAction.CancelAllTransfer)
                            }
                        }
                    }
                }
            }

            Row(modifier = modifier.fillMaxWidth()) {
                Column(
                    modifier = modifier.weight(0.5f)
                        .clickable(
                            enabled = !isCurrentScreenDownload,
                            indication = null,
                            interactionSource = interactionSource
                        ) { onScreenToggle() }
                        .padding(top = 1.percentOfScreenHeight()),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getString(context, R.string.download),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 1.percentOfScreenHeight()),
                        color = if (isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                        thickness = 1.dp
                    )
                }

                Column(
                    modifier = modifier.weight(0.5f)
                        .clickable(
                            enabled = isCurrentScreenDownload,
                            indication = null,
                            interactionSource = interactionSource
                        ) { onScreenToggle() }
                        .padding(top = 1.percentOfScreenHeight()),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getString(context, R.string.upload),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (!isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 1.percentOfScreenHeight()),
                        color = if (!isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun FileTransferTopBarLandscape(
    modifier: Modifier = Modifier,
    isCurrentScreenDownload: Boolean,
    isUploadsPaused: Boolean,
    isDownloadsPaused: Boolean,
    isAllTransferPaused: Boolean,
    onTransferAction: (TransferAction) -> Unit,
    onBackClick: () -> Unit,
    onScreenToggle: () -> Unit
) {
    val context = LocalContext.current
    var offset = Offset.Zero
    var showMoreOption by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.percentOfScreenWidth()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(0.1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.back_icon),
                            contentDescription = getString(context, R.string.back_icon),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = getString(context, R.string.transfer),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (!isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(0.2f),
                    horizontalArrangement = Arrangement.End
                ) {

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        IconButton(onClick = {
                            if (isCurrentScreenDownload) {
                                onTransferAction(TransferAction.ToggleSpecificTransfers(TransferType.DOWNLOAD))
                                return@IconButton
                            }
                            onTransferAction(TransferAction.ToggleSpecificTransfers(TransferType.UPLOAD))
                        }) {
                            if (isCurrentScreenDownload) {
                                Icon(
                                    imageVector = if (isDownloadsPaused) Icons.Outlined.PlayArrow
                                    else Icons.Outlined.Pause,
                                    contentDescription = getString(context, R.string.download_icon),
                                    modifier = Modifier.size(30.dp)
                                )
                                return@IconButton
                            }

                            Icon(
                                imageVector = if (isUploadsPaused) Icons.Outlined.PlayArrow
                                else Icons.Outlined.Pause,
                                contentDescription = getString(context, R.string.download_icon),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
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

                        FileTransferDropDownMenu(
                            modifier = Modifier,
                            offset = DpOffset(
                                x = with(LocalDensity.current) { offset.x.toDp()  },
                                y = with(LocalDensity.current) { offset.y.toDp()  }
                            ),
                            isDownloadScreen = isCurrentScreenDownload,
                            expanded = showMoreOption,
                            isAllTransferPaused = isAllTransferPaused,
                            onDismiss = { showMoreOption = false }
                        ) {
                            showMoreOption = false
                            when (it) {
                                TransferActions.ToggleAllTransfer -> onTransferAction(TransferAction.ToggleAllTransfer)
                                TransferActions.CancelUploads -> {
                                    onTransferAction(TransferAction.CancelSpecificTransfers(TransferType.UPLOAD))
                                }
                                TransferActions.CancelDownloads -> {
                                    onTransferAction(TransferAction.CancelSpecificTransfers(TransferType.DOWNLOAD))
                                }
                                TransferActions.CancelAllTransfer -> onTransferAction(TransferAction.CancelAllTransfer)
                            }
                        }
                    }
                }
            }

            Row(modifier = modifier.fillMaxWidth()) {
                Column(
                    modifier = modifier.weight(0.5f)
                        .clickable(
                            enabled = !isCurrentScreenDownload,
                            indication = null,
                            interactionSource = interactionSource
                        ) { onScreenToggle() }
                        .padding(top = 1.percentOfScreenHeight()),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getString(context, R.string.download),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 1.percentOfScreenHeight()),
                        color = if (isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                        thickness = 1.dp
                    )
                }

                Column(
                    modifier = modifier.weight(0.5f)
                        .clickable(
                            enabled = isCurrentScreenDownload,
                            indication = null,
                            interactionSource = interactionSource
                        ) { onScreenToggle() }
                        .padding(top = 1.percentOfScreenHeight()),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getString(context, R.string.upload),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (!isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 1.percentOfScreenHeight()),
                        color = if (!isCurrentScreenDownload) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}


@Composable
fun FileTransferDropDownMenu(
    modifier: Modifier = Modifier,
    offset: DpOffset,
    isDownloadScreen: Boolean,
    expanded: Boolean = false,
    isAllTransferPaused: Boolean,
    onDismiss: () -> Unit,
    onClick: (TransferActions) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.percentOfScreenWidth()),
        contentAlignment = Alignment.TopEnd
    ) {
        DropdownMenu(expanded = expanded,
            offset = offset,
            onDismissRequest = onDismiss,
            modifier = Modifier,
        ) {
            TransferActions.entries.filterNot {
                it == if (isDownloadScreen)  TransferActions.CancelUploads
                else TransferActions.CancelDownloads
            }.forEach { item ->
                DropdownMenuItem(
                    text = {
                        val text = if (item == TransferActions.ToggleAllTransfer) {
                            if (isAllTransferPaused) "Resume All" else "Pause All"
                        } else {
                            item.displayName
                        }
                        Text(text = text, style = MaterialTheme.typography.labelMedium)
                    },
                    onClick = { onClick(item) }
                )
            }

        }
    }

}