package com.niclauscott.jetdrive.features.auth.ui.screen.register.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import com.niclauscott.jetdrive.features.auth.domain.model.dto.RegisterRequestDTO
import com.niclauscott.jetdrive.features.auth.ui.screen.component.AuthHeaderSection

@Composable
fun RegisterScreenLandscape(
    modifier: Modifier = Modifier,
    isRegistering: Boolean,
    onLoginClicked: () -> Unit,
    onRegisterClicked: (RegisterRequestDTO) -> Unit,
) {
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.padding(bottom = if (!imeVisible) 16.dp else 0.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        AnimatedVisibility(visible = !imeVisible) {
            AuthHeaderSection(
                modifier = Modifier.fillMaxWidth(),
                title = "Sign Up",
                description = "Create a Jet Drive account."
            )
        }

        RegisterFormSectionLandscape(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding(),
            isRegistering = isRegistering,
            imeVisible = imeVisible,
            onLoginClicked = onLoginClicked,
            onRegisterClicked = onRegisterClicked
        )
    }
}

@Composable
fun RegisterFormSectionLandscape(
    modifier: Modifier = Modifier,
    isRegistering: Boolean,
    imeVisible: Boolean,
    onLoginClicked: () -> Unit,
    onRegisterClicked: (RegisterRequestDTO) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var emailText by remember { mutableStateOf("") }
    var firstNameText by remember { mutableStateOf("") }
    var lastNameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var confirmPasswordText by remember { mutableStateOf("") }

    val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$")
    val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    var emailError by remember { mutableStateOf(false) }
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .consumeWindowInsets(WindowInsets.statusBars),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            JetDriveTextField(
                text = firstNameText,
                onValueChange = {
                    firstNameError = it.length < 3 || it.any { char -> char.isDigit()  }
                    firstNameText = it
                },
                label = "First Name",
                hint = "John",
                isError = firstNameError,
                modifier = Modifier
                    .weight(0.5f)
            )

            Spacer(modifier = Modifier.weight(0.05f))

            JetDriveTextField(
                text = lastNameText,
                onValueChange = {
                    lastNameError = it.length < 3 || it.any { char -> char.isDigit()  }
                    lastNameText = it
                },
                label = "Last Name",
                hint = "Doe",
                isError = lastNameError,
                modifier = Modifier
                    .weight(0.5f)
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

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

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
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
                    .weight(0.5f)
            )

            Spacer(modifier = Modifier.weight(0.05f))

            JetDrivePasswordField(
                text = confirmPasswordText,
                onValueChange = {
                    confirmPasswordError = !it.matches(passwordPattern) && it == passwordText
                    confirmPasswordText = it
                },
                label = "Confirm Password",
                hint = "Password",
                isError = confirmPasswordError,
                modifier = Modifier
                    .weight(0.5f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        JetDriveButton(
            text = "Sign Up",
            isLoading = isRegistering,
            onClick = {
                keyboardController?.hide()
                firstNameError = firstNameText.length < 3 || firstNameText.any { char -> char.isDigit()  }
                lastNameError = lastNameText.length < 3 || lastNameText.any { char -> char.isDigit()  }
                emailError = !emailText.matches(emailPattern)
                passwordError = !passwordText.matches(passwordPattern)
                confirmPasswordError =
                    !passwordText.matches(passwordPattern) || confirmPasswordText != passwordText

                if (firstNameError || lastNameError || emailError || passwordError) return@JetDriveButton

                onRegisterClicked(RegisterRequestDTO(emailText, passwordText, firstNameText, lastNameText))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(visible = !imeVisible) {
            JetDriveLink(
                text = "Already have an account?",
                onClick = onLoginClicked,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}