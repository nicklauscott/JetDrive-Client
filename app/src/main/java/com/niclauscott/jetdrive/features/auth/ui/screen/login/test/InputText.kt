package com.niclauscott.jetdrive.features.auth.ui.screen.login.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niclauscott.jetdrive.core.ui.theme.customFontFamily


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InputText(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    maxLine: Int = 1,
    isFieldBlank: Boolean?,
    keyboardType: KeyboardType = KeyboardType.Text,
    interactionSource: MutableInteractionSource,
    onTextChange: (String) -> Unit,
    onImeAction: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(value = text,
        textStyle = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 18.sp, fontFamily = customFontFamily[3]
        ),
        onValueChange = onTextChange,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEEEFF1),
            focusedTextColor = Color(0xFF111213),
            //containerColor = Color(0xFFEEEFF1),
            //textColor = Color(0xFF000000)

        ),
        maxLines = maxLine,
        label = { if (!interactionSource.collectIsFocusedAsState().value) {
            Text(text = label,
                color = Color(0xFF000000).copy(alpha = 0.5f),
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp, fontFamily = customFontFamily[3]
                ))
        }},
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone =  {
            onImeAction()
            keyboardController?.hide()
        }),
        modifier = modifier.clip(shape = RoundedCornerShape(corner = CornerSize(6.dp)))
            .border(
                border = BorderStroke(width = if (isFieldBlank == true) 1.5.dp else 0.dp,
                    color = if (isFieldBlank == true) Color.Red else Color.Transparent)
            )
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PasswordInputField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    maxLine: Int = 1,
    isFieldBlank: Boolean?,
    keyboardType: KeyboardType = KeyboardType.Password,
    interactionSource: MutableInteractionSource,
    onTextChange: (String) -> Unit,
    onImeAction: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(value = text,
        onValueChange = onTextChange,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEEEFF1),
            focusedTextColor = Color(0xFF111213),
            //containerColor = Color(0xFFEEEFF1),
            //textColor = Color(0xFF000000)

        ),
        interactionSource = interactionSource,
        maxLines = maxLine,
        label = { if (!interactionSource.collectIsFocusedAsState().value) {
            Text(text = label,
                color = Color(0xFF000000).copy(alpha = 0.5f),
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp, fontFamily = customFontFamily[3]
                ))
        }},
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone =  {
            onImeAction()
            keyboardController?.hide()
        }),
        visualTransformation = PasswordVisualTransformation(),
        modifier = modifier.clip(shape = RoundedCornerShape(corner = CornerSize(6.dp)))
            .border(
                border = BorderStroke(width = if (isFieldBlank == true) 1.5.dp else 0.dp,
                    color = if (isFieldBlank == true) Color.Red else Color.Transparent)
            )
    )
}