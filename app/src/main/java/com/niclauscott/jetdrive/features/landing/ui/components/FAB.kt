package com.niclauscott.jetdrive.features.landing.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth

@Composable
fun FAB(
    showActiveFileOperationFAB: Boolean,
    progress: Float,
    onClickActiveFileOperationFAB: () -> Unit,
    showFileOperationFAB: Boolean,
    onClick: () -> Unit
) {
   Column {
       if (showActiveFileOperationFAB) {
           ActiveFileOperationFAB(modifier = Modifier, progress = progress, onClick = onClickActiveFileOperationFAB)
       }
       if (showActiveFileOperationFAB && showFileOperationFAB) {
           Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))
       }
       if (showFileOperationFAB) {
           FileOperationFAB(modifier = Modifier, onClick = onClick)
       }
   }
}

@Composable
fun ActiveFileOperationFAB(
    modifier: Modifier = Modifier,
    progress: Float,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    Card(
        modifier = modifier
            .size(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        shape = CircleShape,
        onClick =  onClick
    ) {
        val backgroundColor = MaterialTheme.colorScheme.inverseOnSurface
        val color = MaterialTheme.colorScheme.onPrimary
        Box(
            modifier = Modifier
                .padding(5.dp)
                .drawBehind {
                    val size = size.minDimension
                    size / 2
                    val topLeft =
                        Offset((this.size.width - size) / 2, (this.size.height - size) / 2)
                    val arcSize = Size(size, size)
                    val stroke = with(density) { 2.dp.toPx() }

                    drawArc(
                        color = backgroundColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )

                }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.download_upload),
                contentDescription = getString(context, R.string.active_file_operation),
                modifier = Modifier.size(55.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FileOperationFAB(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .size(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        shape = CircleShape,
        onClick =  onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.add_icon),
                contentDescription = getString(context, R.string.active_file_operation),
                modifier = Modifier.size(35.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}


