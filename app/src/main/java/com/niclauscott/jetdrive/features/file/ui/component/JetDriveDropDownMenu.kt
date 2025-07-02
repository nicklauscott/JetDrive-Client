package com.niclauscott.jetdrive.features.file.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenWidth


@Composable
fun CustomDropDownMenu(
    modifier: Modifier = Modifier,
    selectedItem: String,
    items: List<String>,
    onClick: (String) -> Unit
) {

    val expanded = remember { mutableStateOf(false) }
    var offset = Offset.Zero
    val context = LocalContext.current

    Row(modifier = modifier.width(20.percentOfScreenWidth())
        .padding(vertical = 1.percentOfScreenHeight())
        .clip(RoundedCornerShape(4.dp))
        .pointerInteropFilter {
            offset = Offset(it.x, it.y)
            false
        }
        .clickable { expanded.value = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .weight(0.7f)
            .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = selectedItem,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
        Column(modifier = Modifier
            .weight(0.3f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center) {
            Icon(imageVector = Icons.Default.ArrowDropDown,
                contentDescription = getString(context, R.string.show_more),
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.size(25.dp)
            )
        }

        DropdownMenu(expanded = expanded.value,
            offset = DpOffset(
                x = with(LocalDensity.current) { offset.x.toDp()  },
                y = with(LocalDensity.current) { offset.y.toDp()  } // Push down
            ),
            onDismissRequest = { expanded.value = false },
            modifier = Modifier,
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item,
                            style = MaterialTheme.typography.labelMedium
                        ) },
                    onClick = {
                        expanded.value = false
                        onClick(item)
                    })
            }

        }
    }
}

@Composable
fun CustomDropDownMenu(
    modifier: Modifier = Modifier,
    offset: DpOffset,
    expanded: Boolean = false,
    items: List<String>,
    onDismiss: () -> Unit,
    onClick: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .padding(2.percentOfScreenWidth()),
        contentAlignment = Alignment.TopEnd
    ) {
        DropdownMenu(expanded = expanded,
            offset = offset,
            onDismissRequest = onDismiss,
            modifier = Modifier,
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item,
                            style = MaterialTheme.typography.labelMedium
                        ) },
                    onClick = { onClick(item) }
                )
            }

        }
    }

}