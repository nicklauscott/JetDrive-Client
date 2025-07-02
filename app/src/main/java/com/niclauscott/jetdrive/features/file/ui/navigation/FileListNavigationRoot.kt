package com.niclauscott.jetdrive.features.file.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.niclauscott.jetdrive.features.file.ui.screen.copy_move.FileCopyMoveScreen
import com.niclauscott.jetdrive.features.file.ui.screen.copy_move.FileCopyMoveScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.FileDetailScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.FileScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.FileScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.CopyMoveScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DetailScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileListScreen
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FileListNavigationRoot(
    modifier: Modifier = Modifier,
    landingScreenViewModel: LandingScreenViewModel,
) {
    val backStack = landingScreenViewModel.fileScreenBackStack
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
                is FileListScreen -> {
                    NavEntry(key = key) {
                        val viewModel: FileScreenViewModel = koinViewModel {
                            parametersOf(key.fileNode, backStack)
                        }
                        FileScreen(viewModel = viewModel)
                    }
                }
                is DetailScreen -> {
                    NavEntry(key = key) {
                        FileDetailScreen(
                            fileNode = key.fileNode,
                            landingScreenViewModel = landingScreenViewModel
                        ) { backStack.removeAt(backStack.lastIndex) }
                    }
                }

                is CopyMoveScreen -> {
                    NavEntry(key = key) {
                        val viewModel: FileCopyMoveScreenViewModel = koinViewModel {
                            parametersOf(
                                key.fileNode, key.folderId, key.folderName, key.action, backStack,
                                landingScreenViewModel
                            )
                        }
                        FileCopyMoveScreen(
                            action = key.action,
                            viewModel = viewModel
                        )
                    }
                }

                else -> throw RuntimeException("Invalid NavKey.")
            }
        }
    )
}