package com.niclauscott.jetdrive.features.landing.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.niclauscott.jetdrive.features.landing.ui.components.ActionsBottomSheet
import com.niclauscott.jetdrive.features.landing.ui.navigation.FileScreen
import com.niclauscott.jetdrive.features.landing.ui.navigation.HomeScreen
import com.niclauscott.jetdrive.features.landing.ui.navigation.LandingScreenNavigationRoot
import com.niclauscott.jetdrive.features.landing.ui.navigation.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(modifier: Modifier = Modifier, viewModel: LandingScreenViewModel) {

    val backStack = rememberNavBackStack(HomeScreen)
    val showBottomBar by viewModel.showBottomBar.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomNavigationBar(viewModel.state.value.currentScreen) { selectedScreen ->
                    when (selectedScreen) {
                        0 -> {
                            viewModel.changeScreen(0)
                            backStack.clear()
                            backStack.add(HomeScreen)
                        }
                        1 -> {
                            viewModel.changeScreen(1)
                            backStack.clear()
                            backStack.add(FileScreen)
                        }
                        2 -> {
                            viewModel.changeScreen(2)
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
            ActionsBottomSheet(
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
            landingScreenViewModel = viewModel
        )
    }
}