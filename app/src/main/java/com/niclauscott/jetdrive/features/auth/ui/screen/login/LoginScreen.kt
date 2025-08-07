package com.niclauscott.jetdrive.features.auth.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.features.auth.ui.screen.login.component.LoginScreenLandscape
import com.niclauscott.jetdrive.features.auth.ui.screen.login.component.LoginScreenPortrait
import com.niclauscott.jetdrive.features.auth.ui.screen.login.state.LoginScreenUIEffect
import com.niclauscott.jetdrive.features.auth.ui.screen.login.state.LoginScreenUIEvent

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    email: String? = null,
    viewModel: LoginScreenVieModel
) {
    val context = LocalContext.current
    var toast by remember { mutableStateOf<Toast?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginScreenUIEffect.ShowSnackbar -> {
                    toast?.cancel()
                    toast = Toast.makeText(context, effect.message, Toast.LENGTH_SHORT)
                    toast?.show()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->

        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

        when (deviceConfiguration) {
            DeviceConfiguration.MOBILE_PORTRAIT -> {
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
                LoginScreenPortrait(
                    modifier = rootModifier,
                    isLoginIn = viewModel.state.value.isLoginIn,
                    email = email,
                    onRegisterClick = { viewModel.onEvent(LoginScreenUIEvent.Register) },
                    onGoogleLoginClick ={ viewModel.onEvent(LoginScreenUIEvent.GoogleLoginScreen(context)) },
                    onLoginClick = { email, password ->
                        viewModel.onEvent(LoginScreenUIEvent.LoginScreen(email, password))
                    }
                )
            }
            else -> {
                val rootModifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .consumeWindowInsets(WindowInsets.navigationBars)
                LoginScreenLandscape(
                    modifier = rootModifier,
                    isLoginIn = viewModel.state.value.isLoginIn,
                    email = email,
                    onRegisterClick = { viewModel.onEvent(LoginScreenUIEvent.Register) },
                    onGoogleLoginClick ={ viewModel.onEvent(LoginScreenUIEvent.GoogleLoginScreen(context)) },
                    onLoginClick = { email, password ->
                        viewModel.onEvent(LoginScreenUIEvent.LoginScreen(email, password))
                    }
                )
            }
        }

    }

}