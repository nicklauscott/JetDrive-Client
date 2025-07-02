package com.niclauscott.jetdrive.features.file.ui.screen.file_detail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight

@Composable
fun DetailCell(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(modifier = modifier.padding(top = 1.percentOfScreenHeight())) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
        Spacer(modifier = Modifier.width(0.5.percentOfScreenHeight()))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}
