package com.niclauscott.jetdrive.features.auth.ui.screen.login.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.core.ui.component.JetDriveButton
import com.niclauscott.jetdrive.core.ui.component.JetDriveLink
import com.niclauscott.jetdrive.core.ui.component.JetDrivePasswordField
import com.niclauscott.jetdrive.core.ui.component.JetDriveTextField

@Composable
fun LoginFormSection(
    modifier: Modifier = Modifier,
    isLoginIn: Boolean,
    email: String? = null,
    onRegisterClicked: () -> Unit,
    onLoginClicked: (String, String) -> Unit,
) {
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
                emailError = !it.matches(emailPattern)
                emailText = it
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
            isLoginIn = isLoginIn,
            onClick = {
                emailError = !emailText.matches(emailPattern)
                passwordError = !passwordText.matches(passwordPattern)
                if (emailError || passwordError) return@JetDriveButton
                onLoginClicked(emailText, passwordText)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        JetDriveLink(
            text = "Don't have an account?",
            onClick = onRegisterClicked,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}