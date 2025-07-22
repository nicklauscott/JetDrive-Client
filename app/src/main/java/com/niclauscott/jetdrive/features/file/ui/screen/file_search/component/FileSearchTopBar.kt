package com.niclauscott.jetdrive.features.file.ui.screen.file_search.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.ui.component.JetDriveSearchField
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth

@Composable
fun FileSearchTopBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onTextClear: () -> Unit,
    onTextChange: (String) -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    when (deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            FileSearchTopBarPortrait(modifier = modifier, searchText = searchText,
                onTextClear = onTextClear, onTextChange = onTextChange)
        }
        else -> {
            FileSearchTopBarLandscape(modifier = modifier, searchText = searchText,
                onTextClear = onTextClear, onTextChange = onTextChange)
        }
    }
}

@Composable
fun FileSearchTopBarLandscape(
    modifier: Modifier = Modifier,
    searchText: String,
    onTextClear: () -> Unit,
    onTextChange: (String) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .height(15.percentOfScreenHeight())
            .padding(vertical = 0.5.percentOfScreenHeight()),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        JetDriveSearchField(
            text = searchText, onClickCancel = onTextClear,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {})
        ) { onTextChange(it) }
    }
}

@Composable
fun FileSearchTopBarPortrait(
    modifier: Modifier = Modifier,
    searchText: String,
    onTextClear: () -> Unit,
    onTextChange: (String) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .height(8.percentOfScreenHeight())
            .padding(vertical = 2.percentOfScreenWidth()),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        JetDriveSearchField(
            text = searchText,
            onClickCancel = onTextClear,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {})
        ) {
            onTextChange(it)
        }
    }
}