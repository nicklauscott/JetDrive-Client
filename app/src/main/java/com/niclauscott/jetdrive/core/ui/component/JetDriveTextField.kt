package com.niclauscott.jetdrive.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.R

@Composable
fun JetDriveTextField(
    text: String, label: String,
    hint: String, isError: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary
            ),
            maxLines = 1,
            placeholder = {
                Text(text = hint, style = MaterialTheme.typography.bodyLarge)
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
fun JetDrivePasswordField(
    text: String, label: String,
    hint: String, isError: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if(!isPasswordVisible) {
                PasswordVisualTransformation(mask = '*')
            } else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary
            ),
            maxLines = 1,
            placeholder = {
                Text(text = hint, style = MaterialTheme.typography.bodyLarge)
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    when {
                        isPasswordVisible -> {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Hide password"
                            )
                        }
                        !isPasswordVisible -> {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Show password"
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun JetDriveSearchField(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "Search",
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onClickCancel: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = text,
        onValueChange = onValueChange,

        visualTransformation = VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent

        ),
        placeholder = {
            Text(
                text = hint, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        },
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp),
        trailingIcon = {
            Box(contentAlignment = Alignment.Center) {
                IconButton(onClick = onClickCancel) {
                    Icon(
                        painter = painterResource(R.drawable.clear_icon),
                        contentDescription = "Hide password"
                    )
                }
            }
        },
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions
    )
}
