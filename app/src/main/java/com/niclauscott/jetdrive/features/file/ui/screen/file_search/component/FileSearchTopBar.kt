package com.niclauscott.jetdrive.features.file.ui.screen.file_search.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.ui.component.JetDriveSearchField
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight

@Composable
fun FileSearchTopBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onTextClear: () -> Unit,
    onTextChange: (String) -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth()
            .height(8.percentOfScreenHeight()),
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