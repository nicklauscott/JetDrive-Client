package com.niclauscott.jetdrive.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.niclauscott.jetdrive.features.auth.ui.screen.login.LoginScreen
import com.niclauscott.jetdrive.features.auth.ui.screen.register.RegisterScreen
import com.niclauscott.jetdrive.core.splash.domain.SplashScreenViewModel
import com.niclauscott.jetdrive.features.auth.ui.screen.login.LoginScreenVieModel
import com.niclauscott.jetdrive.features.auth.ui.screen.register.RegistrationScreenVieModel
import com.niclauscott.jetdrive.features.landing.ui.LandingScreen
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    splashScreenViewModel: SplashScreenViewModel
) {
    val startScreen = splashScreenViewModel.screen.value

    if (startScreen != null) {
        val backStack = rememberNavBackStack(startScreen)

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
                    is LoginScreen -> {
                        NavEntry(key = key) {
                            val viewModel: LoginScreenVieModel = koinViewModel { parametersOf(backStack) }
                            LoginScreen(viewModel = viewModel)
                        }
                    }

                    is RegisterScreen -> {
                        NavEntry(key = key) {
                            val viewModel: RegistrationScreenVieModel = koinViewModel { parametersOf(backStack) }
                            RegisterScreen(viewModel = viewModel)
                        }
                    }

                    is LandingScreen -> {
                        NavEntry(key = key) {
                            val viewModel: LandingScreenViewModel = koinViewModel()
                            LandingScreen(viewModel = viewModel)
                        }
                    }

                    else -> throw RuntimeException("Invalid NavKey.")
                }
            }
        )
    }
}