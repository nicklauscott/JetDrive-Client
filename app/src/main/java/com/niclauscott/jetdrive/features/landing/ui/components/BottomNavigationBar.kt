package com.niclauscott.jetdrive.features.landing.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight

enum class AppDestinations(
    @StringRes val title: Int,
    val icon: Int,
    val selectedIcon: Int,
    @StringRes val description: Int
) {
    Home(R.string.home_screen, R.drawable.home_icon, R.drawable.home_filled_icon, R.string.home_description),
    File(R.string.file_screen,R.drawable.file_icon, R.drawable.file_filled_icon, R.string.file_description),
    Profile(R.string.profile_screen,R.drawable.profile_icon, R.drawable.profile_filled_icon, R.string.profile_description),
}
@Composable
fun BottomNavigationBar2(
    selected: Int,
    onClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondary,
        shadowElevation = 8.dp,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDestinations.entries.forEachIndexed { index, destination ->
                ModernBottomItem(
                    itemIndex = index,
                    selectedItemIndex = selected,
                    appDestinations = destination,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun ModernBottomItem(
    modifier: Modifier = Modifier,
    itemIndex: Int,
    selectedItemIndex: Int,
    appDestinations: AppDestinations,
    onClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val isSelected = itemIndex == selectedItemIndex
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else Color.Transparent,
        animationSpec = tween(200),
        label = "container"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(itemIndex) }
            .animateContentSize(),
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .scale(animatedScale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(
                    if (isSelected) appDestinations.selectedIcon
                    else appDestinations.icon
                ),
                contentDescription = getString(context, appDestinations.description),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = getString(context, appDestinations.title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun BottomNavigationBar1(
    selected: Int,
    onClick: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        tonalElevation = 3.dp
    ) {
        AppDestinations.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            if (index == selected) destination.selectedIcon
                            else destination.icon
                        ),
                        contentDescription = getString(LocalContext.current, destination.description),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = getString(LocalContext.current, destination.title),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = index == selected,
                onClick = { onClick(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun BottomNavigationBar(selected: Int, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .height(8.percentOfScreenHeight()),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        AppDestinations.entries.forEachIndexed { index, bottomItem ->
            BottomItem(
                itemIndex = index,
                selectedItemIndex = selected,
                appDestinations = bottomItem
            ) {
                onClick(it)
            }
        }
    }

}

@Composable
fun BottomItem(
    modifier: Modifier = Modifier,
    itemIndex: Int,
    selectedItemIndex: Int,
    appDestinations: AppDestinations,
    onClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val tint = if (itemIndex == selectedItemIndex) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxHeight()
            .width(100.dp)
            .clickable {
                if (itemIndex != selectedItemIndex) {
                    onClick(itemIndex)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                if (itemIndex != selectedItemIndex) appDestinations.icon
                else appDestinations.selectedIcon
            ),
            contentDescription = getString(context, appDestinations.description),
            tint = tint,
            modifier = Modifier.size(35.dp)
        )
    }
}