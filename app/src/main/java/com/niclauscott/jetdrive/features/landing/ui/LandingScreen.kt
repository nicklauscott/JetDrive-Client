package com.niclauscott.jetdrive.features.landing.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.niclauscott.jetdrive.features.landing.ui.components.LandingScreenBottomSheet
import com.niclauscott.jetdrive.features.landing.ui.navigation.FileScreen
import com.niclauscott.jetdrive.features.landing.ui.navigation.HomeScreen
import com.niclauscott.jetdrive.features.landing.ui.navigation.LandingScreenNavigationRoot
import com.niclauscott.jetdrive.features.landing.ui.navigation.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(modifier: Modifier = Modifier, viewModel: LandingScreenViewModel) {

    val backStack = rememberNavBackStack(HomeScreen)
    var showFileActionFAB by remember { mutableStateOf(true) }
    val showBottomBar by viewModel.showBottomBar.collectAsState()
    val showFab by viewModel.showFab.collectAsState()
    var currentScreenIndex by remember { mutableIntStateOf(0) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        floatingActionButton = {
            AnimatedContent(showFab) { target ->
                if (target) {
                    AnimatedVisibility(
                        visible = showBottomBar,
                        enter = scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        FAB(
                            showActiveFileOperationFAB = viewModel.activeFileOperation.value,
                            progress = viewModel.activeFileOperationProgress.value,
                            onClickActiveFileOperationFAB = {},
                            showFileOperationFAB = showFileActionFAB
                        ) { showBottomSheet = true }
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
                            viewModel.showFab()
                            showFileActionFAB = true
                            backStack.clear()
                            backStack.add(HomeScreen)
                        }
                        1 -> {
                            currentScreenIndex = 1
                            viewModel.hideFab()
                            backStack.clear()
                            backStack.add(FileScreen)
                        }
                        2 -> {
                            currentScreenIndex = 2
                            viewModel.showFab()
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

        if (showBottomSheet) {
            LandingScreenBottomSheet(
                modifier = Modifier,
                sheetState = sheetState,
                onDismiss = { showBottomSheet = false }
            ) {

                showBottomSheet = false
            }
        }

        LandingScreenNavigationRoot(
            modifier = innerModifier,
            backStack = backStack,
            viewModel = viewModel
        )
    }
}