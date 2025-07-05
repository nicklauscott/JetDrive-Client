package com.niclauscott.jetdrive.features.auth.ui.screen.register

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.ui.component.CustomSnackbarHost
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.features.auth.ui.screen.component.AuthHeaderSection
import com.niclauscott.jetdrive.features.auth.ui.screen.register.component.RegisterFormSection
import com.niclauscott.jetdrive.features.auth.ui.screen.register.state.RegisterScreenUIEffect
import com.niclauscott.jetdrive.features.auth.ui.screen.register.state.RegisterScreenUIEvent

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationScreenVieModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterScreenUIEffect.ShowSnackbar -> {
                    Log.e("SplashScreenViewModel", "LoginScreen -> effect: ${effect.message}")
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            CustomSnackbarHost(snackbarHostState = snackbarHostState)
        }
    ) { innerPadding ->
        val rootModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp
            )
            .consumeWindowInsets(WindowInsets.navigationBars)

        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

        when (deviceConfiguration) {
            DeviceConfiguration.MOBILE_PORTRAIT -> {
                Column(
                    modifier = rootModifier.padding(top = if (!imeVisible) 32.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    AnimatedVisibility(visible = !imeVisible) {
                        AuthHeaderSection(
                            modifier = Modifier.fillMaxWidth(),
                            title = "Sign Up",
                            description = "Create a Jet Drive account."
                        )
                    }

                    RegisterFormSection(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .imePadding(),
                        isLoginIn = viewModel.state.value.isRegistering,
                        hideButtonLink = imeVisible,
                        onLoginClicked = { viewModel.onEvent(RegisterScreenUIEvent.Login) }
                    ) { registerRequest ->
                        viewModel.onEvent(RegisterScreenUIEvent.RegisterScreen(registerRequest))
                    }
                }
            }
            DeviceConfiguration.MOBILE_LANDSCAPE -> {}
            else -> {}
        }

    }

}




