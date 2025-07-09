package com.niclauscott.jetdrive.features.landing.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.landing.ui.state.FileActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onActionClicked: (FileActions) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.fillMaxWidth(),
        sheetState = sheetState,
        shape = RectangleShape,
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.inverseOnSurface) },
        scrimColor = Color.Black.copy(alpha = 0.9f)
    ) {
        Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))
        FileActions.entries.forEach { action ->
            ActionCell(action = action) { onActionClicked(action) }
        }
    }
}

@Composable
fun ActionCell(
    modifier: Modifier = Modifier,
    action: FileActions,
    enabled: Boolean = true,
    onClick: (FileActions) -> Unit
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Dynamic colors based on action type
    val iconColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.primary
    }

    val textColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val backgroundColor = when {
        !enabled -> Color.Transparent
        isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    color = MaterialTheme.colorScheme.primary
                ),
                enabled = enabled,

            ) {
                onClick(action)
            },
        color = backgroundColor,
        tonalElevation = if (isPressed) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 4.percentOfScreenWidth(),
                    vertical = 2.5.percentOfScreenHeight()
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.percentOfScreenWidth())
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(action.icon),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 1.percentOfScreenWidth()),
                verticalArrangement = Arrangement.spacedBy(0.5.percentOfScreenHeight())
            ) {
                // Primary text
                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )


            }
        }
    }
}

@Composable
fun ActionCell1(
    modifier: Modifier = Modifier,
    action: FileActions,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 3.percentOfScreenWidth())
            .padding(vertical = 2.percentOfScreenHeight()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(modifier = Modifier.weight(0.1f), contentAlignment = Alignment.CenterStart) {
            Icon(
                painter = painterResource(action.icon),
                contentDescription = getString(context, R.string.action_icon),
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.size(30.dp)
            )
        }

        Box(modifier = Modifier.weight(0.9f), contentAlignment = Alignment.CenterStart) {
            Text(
                action.description, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }
}