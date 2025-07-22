package com.niclauscott.jetdrive.features.landing.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight

enum class AppDestinations(
    @StringRes val title: Int, val icon: Int, val selectedIcon: Int, @StringRes val description: Int
) {
    Home(R.string.home_screen, R.drawable.home_icon, R.drawable.home_filled_icon, R.string.home_description),
    File(R.string.file_screen,R.drawable.file_icon, R.drawable.file_filled_icon, R.string.file_description),
    Profile(R.string.profile_screen,R.drawable.profile_icon, R.drawable.profile_filled_icon, R.string.profile_description),
}

@Composable
fun BottomNavigationBar(selected: Int, onClick: (Int) -> Unit) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .height(
                if (deviceConfiguration == DeviceConfiguration.MOBILE_PORTRAIT) 8.percentOfScreenHeight()
                else 15.percentOfScreenHeight()
            ),
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
    modifier: Modifier = Modifier, itemIndex: Int, selectedItemIndex: Int,
    appDestinations: AppDestinations, onClick: (Int) -> Unit
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