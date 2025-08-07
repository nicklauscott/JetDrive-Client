package com.niclauscott.jetdrive.features.auth.ui.screen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AuthHeaderSection(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start,
    title: String, description: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(text = description, style = MaterialTheme.typography.bodyLarge)
    }
}
