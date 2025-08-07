package com.niclauscott.jetdrive.features.auth.ui.screen.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.ui.component.JetDriveButton
import com.niclauscott.jetdrive.core.ui.component.JetDriveLink
import com.niclauscott.jetdrive.core.ui.component.JetDrivePasswordField
import com.niclauscott.jetdrive.core.ui.component.JetDriveTextField
import com.niclauscott.jetdrive.features.auth.ui.screen.component.AuthHeaderSection


@Composable
fun LoginScreenPortrait(
    modifier: Modifier = Modifier,
    isLoginIn: Boolean,
    email: String? = null,
    onRegisterClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onLoginClick: (String, String) -> Unit,
) {

    Column(
        modifier = modifier.padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        AuthHeaderSection(
            modifier = Modifier.fillMaxWidth(),
            title = "Sign In",
            description = "Take control of your files."
        )

        LoginFormSection(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            email = email,
            isLoginIn = isLoginIn,
            onRegisterClick = onRegisterClick,
            onLoginClick = onLoginClick
        )

        OAuth2LoginSectionPortrait(modifier = Modifier.fillMaxWidth()) { onGoogleLoginClick() }
    }
}


@Composable
fun LoginFormSection(
    modifier: Modifier = Modifier,
    isLoginIn: Boolean,
    email: String? = null,
    onRegisterClick: () -> Unit,
    onLoginClick: (String, String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var emailText by remember { mutableStateOf(email ?: "") }
    var passwordText by remember { mutableStateOf("") }

    val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$")
    val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
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
        Spacer(modifier = Modifier.height(16.dp))
        JetDriveLink(
            text = "Don't have an account?",
            onClick = onRegisterClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}