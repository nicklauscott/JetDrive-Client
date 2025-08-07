package com.niclauscott.jetdrive.features.auth.ui.screen.register

import android.widget.Toast
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
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.features.auth.ui.screen.component.AuthHeaderSection
import com.niclauscott.jetdrive.features.auth.ui.screen.register.component.RegisterFormSection
import com.niclauscott.jetdrive.features.auth.ui.screen.register.component.RegisterScreenLandscape
import com.niclauscott.jetdrive.features.auth.ui.screen.register.component.RegisterScreenPortrait
import com.niclauscott.jetdrive.features.auth.ui.screen.register.state.RegisterScreenUIEffect
import com.niclauscott.jetdrive.features.auth.ui.screen.register.state.RegisterScreenUIEvent

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationScreenVieModel
) {
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val scrollState = rememberScrollState()
    var toast by remember { mutableStateOf<Toast?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterScreenUIEffect.ShowSnackbar -> {
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
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .consumeWindowInsets(WindowInsets.navigationBars)
                RegisterScreenPortrait(
                    modifier = rootModifier,
                    isRegistering = viewModel.state.value.isRegistering,
                    onLoginClicked = { viewModel.onEvent(RegisterScreenUIEvent.Login) }
                ) { viewModel.onEvent(RegisterScreenUIEvent.RegisterScreen(it)) }
            }
            else -> {
                val rootModifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .consumeWindowInsets(WindowInsets.statusBars)
                RegisterScreenLandscape(
                    modifier = rootModifier,
                    isRegistering = viewModel.state.value.isRegistering,
                    onLoginClicked = { viewModel.onEvent(RegisterScreenUIEvent.Login) }
                ) { viewModel.onEvent(RegisterScreenUIEvent.RegisterScreen(it)) }
            }
        }

    }

}




