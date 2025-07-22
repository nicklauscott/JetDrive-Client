package com.niclauscott.jetdrive.features.home.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.transfer.domain.model.constant.calculatePercentage

@Composable
fun MinimalStatsCard(stats: UserFileStats) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getString(context, R.string.storage_overview),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .size(20.dp)
                            .wrapContentSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary storage metric in a highlighted card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getString(context, R.string.storage_used),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = formatFileSize(stats.totalStorageUsed),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Additional info section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                InfoRow(
                    label = getString(context, R.string.most_common_type),
                    value = stats.mostCommonMimeType?.let { mime ->
                        mime.split("/")[0].replaceFirstChar { it.uppercaseChar() }
                    } ?: "N/A",
                    icon = Icons.Default.Category
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    label = getString(context, R.string.last_upload),
                    value = stats.lastUpload?.toLocalDate()?.toString() ?: "N/A",
                    icon = Icons.Default.Schedule
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    label = getString(context, R.string.smallest_file),
                    value = formatFileSize(stats.smallestFileSize.toLong()),
                    icon = Icons.Default.TrendingDown
                )
            }
        }
    }
}

@Composable
fun StatsCard(stats: UserFileStats) {
    val context = LocalContext.current
    val percentage = calculatePercentage(stats.totalStorageUsed, stats.totalStorageSize)
    val percentageFloat = percentage / 100f
    Card(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getString(context, R.string.storage_overview),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { percentageFloat }, gapSize = (-15).dp, strokeWidth = 1.5.dp, modifier = Modifier
                    )
                    Text(
                        text = "${percentage}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary storage metric in a highlighted card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 4.percentOfScreenWidth(),
                            vertical = 2.5.percentOfScreenHeight()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.percentOfScreenHeight())
                ) {
                    // Storage Used Label
                    Text(
                        text = getString(context, R.string.storage_used),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Storage Used Value
                    Text(
                        text = formatFileSize(stats.totalStorageUsed),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Divider with "of" text
                    Row(
                        modifier = Modifier.padding(horizontal = 2.percentOfScreenWidth()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.percentOfScreenWidth())
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )

                        Text(
                            text = "of",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 1.percentOfScreenWidth())
                        )

                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    }

                    // Total Storage Label
                    Text(
                        text = getString(context, R.string.total_storage),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Total Storage Value
                    Text(
                        text = formatFileSize(stats.totalStorageSize),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

            }

            Spacer(modifier = Modifier.height(20.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(0.5f),
                    label = "Total Files",
                    value = stats.totalFile.toString(),
                    icon = Icons.AutoMirrored.Filled.InsertDriveFile
                )

                MetricCard(
                    modifier = Modifier.weight(0.5f),
                    label = getString(context, R.string.folders),
                    value = stats.totalFolder.toString(),
                    icon = Icons.Default.Folder
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(0.5f),
                    label = getString(context, R.string.average_size),
                    value = formatFileSize(stats.averageFileSize.toLong()),
                    icon = Icons.Default.BarChart
                )

                MetricCard(
                    modifier = Modifier.weight(0.5f),
                    label = getString(context, R.string.largest_file),
                    value = formatFileSize(stats.largestFileSize.toLong()),
                    icon = Icons.Default.TrendingUp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Additional info section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                InfoRow(
                    label = getString(context, R.string.most_common_type),
                    value = stats.mostCommonMimeType?.let { mime ->
                        mime.split("/")[0].replaceFirstChar { it.uppercaseChar() }
                    } ?: "N/A",
                    icon = Icons.Default.Category
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    label = getString(context, R.string.last_upload),
                    value = stats.lastUpload?.toLocalDate()?.toString() ?: "N/A",
                    icon = Icons.Default.Schedule
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    label = getString(context, R.string.smallest_file),
                    value = formatFileSize(stats.smallestFileSize.toLong()),
                    icon = Icons.Default.TrendingDown
                )
            }
        }
    }
}

@Composable
fun StorageProgress(
    modifier: Modifier = Modifier,
    totalSize: Long, usedSize: Long
) {
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val color = MaterialTheme.colorScheme.onPrimaryContainer
    val percentage = calculatePercentage(usedSize, totalSize)
    val percentageFloat = percentage / 100f
    Box(
        modifier = modifier
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
                    sweepAngle = percentageFloat,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )

            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.padding(3.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "${percentage}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = arrayOf("KB", "MB", "GB", "TB", "PB", "EB")
    var size = bytes.toDouble() / 1024
    var unitIndex = 0

    while (size >= 1024 && unitIndex < units.lastIndex) {
        size /= 1024
        unitIndex++
    }

    return String.format("%.1f %s", size, units[unitIndex])
}
