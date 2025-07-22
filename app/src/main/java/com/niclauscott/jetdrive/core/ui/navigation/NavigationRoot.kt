package com.niclauscott.jetdrive.core.ui.navigation

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    var toast by remember { mutableStateOf<Toast?>(null) }
    val startScreen = splashScreenViewModel.screen.value

    LaunchedEffect(Unit) {
        splashScreenViewModel.effect.collect { effect ->
            when (effect) {
                is SplashScreenViewModel.SplashScreenEffect.ShowSnackbar -> {
                    toast?.cancel()
                    toast = Toast.makeText(context, effect.message, Toast.LENGTH_SHORT)
                    toast?.show()
                }
            }
        }
    }

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
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullHeight -> fullHeight  },
                    animationSpec = tween(300)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { fullHeight -> fullHeight  },
                    animationSpec = tween(300)
                )
            },
            popTransitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullHeight -> fullHeight  },
                    animationSpec = tween(300)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { fullHeight -> fullHeight  },
                    animationSpec = tween(300)
                )
            },
            entryProvider = { key ->
                when (key) {
                    is Login -> {
                        NavEntry(key = key) {
                            val viewModel: LoginScreenVieModel = koinViewModel { parametersOf(backStack) }
                            LoginScreen(email = key.email, viewModel = viewModel)
                        }
                    }

                    is Register -> {
                        NavEntry(key = key) {
                            val viewModel: RegistrationScreenVieModel = koinViewModel { parametersOf(backStack) }
                            RegisterScreen(viewModel = viewModel)
                        }
                    }

                    is Landing -> {
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