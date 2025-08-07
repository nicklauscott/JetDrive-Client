package com.niclauscott.jetdrive.features.auth.ui.screen.login.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.ui.component.JetDriveButton
import com.niclauscott.jetdrive.core.ui.component.JetDriveLink
import com.niclauscott.jetdrive.core.ui.component.JetDrivePasswordField
import com.niclauscott.jetdrive.core.ui.component.JetDriveTextField
import com.niclauscott.jetdrive.features.auth.ui.screen.component.AuthHeaderSection

@Composable
fun LoginScreenLandscape(
    modifier: Modifier = Modifier,
    isLoginIn: Boolean,
    email: String? = null,
    onRegisterClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onLoginClick: (String, String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier.fillMaxHeight().weight(0.4f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            AnimatedVisibility(visible = !imeVisible) {
                AuthHeaderSection(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Sign In",
                    description = "Take control of your files."
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            JetDriveLink(
                text = "Don't have an account?",
                onClick = onRegisterClick,
                modifier = Modifier.align(Alignment.Start)
            )

            OAuth2LoginSectionPortrait(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                keyboardController?.hide()
                onGoogleLoginClick()
            }
        }

        Column(
            modifier = modifier.fillMaxHeight().weight(0.6f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            LoginFormSectionLandscape(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                email = email,
                imeVisible = imeVisible,
                isLoginIn = isLoginIn,
                onLoginClick = onLoginClick
            )
        }
    }
}

@Composable
fun LoginFormSectionLandscape(
    modifier: Modifier = Modifier,
    isLoginIn: Boolean,
    imeVisible: Boolean,
    email: String? = null,
    onLoginClick: (String, String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var emailText by remember { mutableStateOf(email ?: "") }
    var passwordText by remember { mutableStateOf("") }

    val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$")
    val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(bottom = if (imeVisible) 16.dp else 0.dp)
            .imePadding()
    ) {
        JetDriveTextField(
            text = emailText,
            onValueChange = {
                emailError = !it.trim().matches(emailPattern)
                emailText = it.trim()
            },
            label = "Email",
            hint = "john.doe@example.com",
            isError = emailError,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        JetDrivePasswordField(
            text = passwordText,
            onValueChange = {
                passwordError = !it.matches(passwordPattern)
                passwordText = it
            },
            label = "Password",
            hint = "Password",
            isError = passwordError,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        JetDriveButton(
            text = "Log In",
            isLoading = isLoginIn,
            onClick = {
                keyboardController?.hide()
                emailError = !emailText.matches(emailPattern)
                passwordError = !passwordText.matches(passwordPattern)
                if (emailError || passwordError) return@JetDriveButton
                onLoginClick(emailText, passwordText)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}