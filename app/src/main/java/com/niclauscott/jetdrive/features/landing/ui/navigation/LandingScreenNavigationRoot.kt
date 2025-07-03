package com.niclauscott.jetdrive.features.landing.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.niclauscott.jetdrive.features.file.ui.navigation.FileListNavigationRoot
import com.niclauscott.jetdrive.features.home.ui.HomeScreen
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import com.niclauscott.jetdrive.features.profile.ui.ProfileScreen

@Composable
fun LandingScreenNavigationRoot(
    modifier: Modifier = Modifier, backStack: NavBackStack,
    viewModel: LandingScreenViewModel
) {
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
            rememberSceneSetupNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is HomeScreen -> {
                    NavEntry(key = key) {
                        HomeScreen()
                    }
                }

                is FileScreen -> {
                    NavEntry(key = key) {
                        FileListNavigationRoot(modifier = Modifier, landingScreenViewModel = viewModel)
                    }
                }

                is ProfileScreen -> {
                    NavEntry(key = key) {
                        ProfileScreen()
                    }
                }

                else -> throw RuntimeException("Invalid NavKey.")
            }
        }
    )
}