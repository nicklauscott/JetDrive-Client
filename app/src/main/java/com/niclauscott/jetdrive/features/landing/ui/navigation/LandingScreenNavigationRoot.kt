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
import com.niclauscott.jetdrive.features.file.ui.navigation.PreviewScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.FilePreviewScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.FilePreviewScreenViewModel
import com.niclauscott.jetdrive.features.home.ui.HomeScreen
import com.niclauscott.jetdrive.features.home.ui.HomeScreenViewModel
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import com.niclauscott.jetdrive.features.profile.ui.ProfileScreen
import com.niclauscott.jetdrive.features.profile.ui.ProfileScreenViewModel
import com.niclauscott.jetdrive.features.transfer.ui.TransferScreen
import com.niclauscott.jetdrive.features.transfer.ui.TransferScreenViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LandingScreenNavigationRoot(
    modifier: Modifier = Modifier, backStack: NavBackStack,
    landingScreenViewModel: LandingScreenViewModel
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
                        val viewModel: HomeScreenViewModel = koinViewModel {
                            parametersOf(backStack)
                        }
                        HomeScreen(viewModel = viewModel)
                    }
                }

                is FileScreen -> {
                    NavEntry(key = key) {
                        FileListNavigationRoot(modifier = Modifier, landingScreenViewModel = landingScreenViewModel)
                    }
                }

                is ProfileScreen -> {
                    NavEntry(key = key) {
                        val viewModel: ProfileScreenViewModel = koinViewModel()
                        ProfileScreen(viewModel = viewModel)
                    }
                }

                is PreviewScreen -> {
                    NavEntry(key = key) {
                        val viewModel: FilePreviewScreenViewModel = koinViewModel {
                            parametersOf(key.fileNode, backStack, landingScreenViewModel)
                        }
                        FilePreviewScreen(landingScreenViewModel = landingScreenViewModel, viewModel = viewModel)
                    }
                }

                is Transfer -> {
                    NavEntry(key = key) {
                        val viewModel: TransferScreenViewModel = koinViewModel {
                            parametersOf(backStack, landingScreenViewModel)
                        }
                        TransferScreen(landingScreenViewModel = landingScreenViewModel, viewModel = viewModel)
                    }
                }

                else -> throw RuntimeException("Invalid NavKey.")
            }
        }
    )
}