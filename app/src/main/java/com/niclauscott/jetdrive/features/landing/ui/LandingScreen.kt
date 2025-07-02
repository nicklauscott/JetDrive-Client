package com.niclauscott.jetdrive.features.landing.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.niclauscott.jetdrive.features.landing.ui.components.BottomNavigationBar
import com.niclauscott.jetdrive.features.landing.ui.components.FAB
import com.niclauscott.jetdrive.features.landing.ui.navigation.FileScreen
import com.niclauscott.jetdrive.features.landing.ui.navigation.HomeScreen
import com.niclauscott.jetdrive.features.landing.ui.navigation.LandingScreenNavigationRoot
import com.niclauscott.jetdrive.features.landing.ui.navigation.ProfileScreen

@Composable
fun LandingScreen(modifier: Modifier = Modifier, viewModel: LandingScreenViewModel) {

    val backStack = rememberNavBackStack(HomeScreen)
    var showFileActionFAB by remember { mutableStateOf(true) }
    val showBottomBar by viewModel.showBottomBar.collectAsState()
    val showFab by viewModel.showFab.collectAsState()
    var currentScreenIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        floatingActionButton = {
            AnimatedContent(showFab) { target ->
                if (target) {
                    AnimatedVisibility(
                        visible = showBottomBar,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        FAB(
                            showActiveFileOperationFAB = viewModel.activeFileOperation.value,
                            progress = viewModel.activeFileOperationProgress.value,
                            onClickActiveFileOperationFAB = {},
                            showFileOperationFAB = showFileActionFAB
                        ) { }
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomNavigationBar(currentScreenIndex) { selectedScreen ->
                    when (selectedScreen) {
                        0 -> {
                            currentScreenIndex = 0
                            showFileActionFAB = true
                            backStack.clear()
                            backStack.add(HomeScreen)
                        }
                        1 -> {
                            currentScreenIndex = 1
                            showFileActionFAB = true
                            backStack.clear()
                            backStack.add(FileScreen)
                        }
                        2 -> {
                            currentScreenIndex = 2
                            showFileActionFAB = false
                            backStack.clear()
                            backStack.add(ProfileScreen)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        val innerModifier = Modifier.fillMaxSize().padding(paddingValues)
        LandingScreenNavigationRoot(
            modifier = innerModifier,
            backStack = backStack,
            viewModel = viewModel
        )
    }
}