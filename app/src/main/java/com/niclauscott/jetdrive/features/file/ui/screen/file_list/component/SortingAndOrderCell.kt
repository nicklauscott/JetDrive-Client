package com.niclauscott.jetdrive.features.file.ui.screen.file_list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth
import com.niclauscott.jetdrive.features.file.ui.component.CustomDropDownMenu
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortOrder
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortType

@Composable
fun SortingAndOrderCell(
    modifier: Modifier = Modifier,
    sortType: SortType, sortOrder: SortOrder,
    onSortOrderClick: () -> Unit,
    onSortTypeClick: (SortType) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 1.percentOfScreenWidth()),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CustomDropDownMenu(
            selectedItem = sortType.name,
            items = SortType.entries.map { it.name },
        ) { onSortTypeClick(SortType.valueOf(it)) }

        IconButton(onClick = onSortOrderClick) {
            Icon(
                painter = painterResource(
                    if (sortOrder == SortOrder.ASC) R.drawable.arrow_down_icon
                    else R.drawable.arrow_up_icon
                ),
                contentDescription = getString(context, R.string.sort_order),
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}