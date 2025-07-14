package com.niclauscott.jetdrive.features.transfer.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.database.domain.constant.TransferStatus
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType

@Composable
fun StatusIndicator(status: TransferStatus, type: TransferType) {
    val (color, icon) = when (status) {
        TransferStatus.ACTIVE -> {
            if (type == TransferType.DOWNLOAD) MaterialTheme.colorScheme.primary to Icons.Rounded.Download
            else MaterialTheme.colorScheme.primary to Icons.Rounded.Upload
        }
        TransferStatus.PAUSED -> MaterialTheme.colorScheme.tertiary to Icons.Rounded.Pause
        TransferStatus.FAILED -> MaterialTheme.colorScheme.tertiary to Icons.Rounded.ErrorOutline
        TransferStatus.COMPLETED -> Color(0xFF4CAF50) to Icons.Rounded.CheckCircle
        else -> MaterialTheme.colorScheme.outline to Icons.Rounded.Schedule
    }

    Box(
        modifier = Modifier
            .size(20.dp)
            .background(
                color = color.copy(alpha = 0.2f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
    }
}