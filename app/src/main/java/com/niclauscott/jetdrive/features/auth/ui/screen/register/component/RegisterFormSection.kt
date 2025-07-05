package com.niclauscott.jetdrive.features.auth.ui.screen.register.component

import androidx.compose.animation.AnimatedVisibility
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
import com.niclauscott.jetdrive.features.auth.domain.model.dto.RegisterRequestDTO

@Composable
fun RegisterFormSection(
    modifier: Modifier = Modifier,
    isLoginIn: Boolean,
    hideButtonLink: Boolean,
    onLoginClicked: () -> Unit,
    onRegisterClicked: (RegisterRequestDTO) -> Unit,
) {
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
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
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        JetDriveButton(
            text = "Sign Up",
            isLoginIn = isLoginIn,
            onClick = {
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
        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = !hideButtonLink) {
            JetDriveLink(
                text = "Already have an account?",
                onClick = onLoginClicked,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}