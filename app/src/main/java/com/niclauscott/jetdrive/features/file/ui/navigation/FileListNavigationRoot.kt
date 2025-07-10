package com.niclauscott.jetdrive.features.file.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.FileCopyMoveScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.FileCopyMoveScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.FileDetailScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.FileDetailScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.FileListScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.FileScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.FilePreviewScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.FilePreviewScreenViewModel
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.FileSearchScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.FileSearchScreenViewModel
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
                            parametersOf(key.fileNode, landingScreenViewModel, backStack)
                        }
                        FileListScreen(modifier = modifier, viewModel = viewModel)
                    }
                }
                is DetailScreen -> {
                    NavEntry(key = key) {
                        val viewModel: FileDetailScreenViewModel = koinViewModel {
                            parametersOf(key.fileNode, backStack, landingScreenViewModel)
                        }
                        FileDetailScreen(landingScreenViewModel = landingScreenViewModel,viewModel = viewModel)
                    }
                }

                is CopyMoveScreen -> {
                    NavEntry(key = key) {
                        val viewModel: FileCopyMoveScreenViewModel = koinViewModel {
                            parametersOf(
                                key.fileNode, key.folderId, key.folderName, key.action,
                                landingScreenViewModel, backStack
                            )
                        }
                        FileCopyMoveScreen(action = key.action, viewModel = viewModel)
                    }
                }

                is SearchScreen -> {
                    NavEntry(key = key) {
                        val viewModel: FileSearchScreenViewModel = koinViewModel {
                            parametersOf(backStack, landingScreenViewModel)
                        }
                        FileSearchScreen(viewModel = viewModel)
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

                else -> throw RuntimeException("Invalid NavKey.")
            }
        }
    )

    /*
    AnimatedContent(
        targetState = backStack.lastOrNull(),
        transitionSpec = {
            // You can switch this logic to pop vs push if needed
            (slideInHorizontally { fullWidth -> fullWidth } + fadeIn()).togetherWith(
                slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut())
        },
        contentKey = { it } // Required to trigger animation on key change
    ) { _ ->
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
                                parametersOf(key.fileNode, landingScreenViewModel, backStack)
                            }
                            FileListScreen(modifier = modifier, viewModel = viewModel)
                        }
                    }
                    is DetailScreen -> {
                        NavEntry(key = key) {
                            val viewModel: FileDetailScreenViewModel = koinViewModel {
                                parametersOf(key.fileNode, backStack, landingScreenViewModel)
                            }
                            FileDetailScreen(landingScreenViewModel = landingScreenViewModel,viewModel = viewModel)
                        }
                    }

                    is CopyMoveScreen -> {
                        NavEntry(key = key) {
                            val viewModel: FileCopyMoveScreenViewModel = koinViewModel {
                                parametersOf(
                                    key.fileNode, key.folderId, key.folderName, key.action,
                                    landingScreenViewModel, backStack
                                )
                            }
                            FileCopyMoveScreen(action = key.action, viewModel = viewModel)
                        }
                    }

                    is SearchScreen -> {
                        NavEntry(key = key) {
                            val viewModel: FileSearchScreenViewModel = koinViewModel {
                                parametersOf(backStack, landingScreenViewModel)
                            }
                            FileSearchScreen(viewModel = viewModel)
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

                    else -> throw RuntimeException("Invalid NavKey.")
                }
            }
        )
    }

     */

}