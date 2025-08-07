package com.niclauscott.jetdrive.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF0A0A0A),
    inverseSurface = Color(0xFF1A1A1A),
    inverseOnSurface = Color(0xFF999999),
    onBackground = Color(0xFFF0F0F0),
    onTertiaryContainer = Color(0xFFF0F0F0),
    primary = Color(0xFF1DB5E0),
    onPrimary = Color(0xFF002E3A),
    secondary = Color(0xFF2B2D32),
    onSecondary = Color(0xFFB0B3B8),
    tertiary = Color(0xFFAE7CFF),
    errorContainer = Color(0xFF3A1E1E),
    onErrorContainer = Color(0xFFFFBDBD)
)

val LightColorScheme = lightColorScheme(
    background = Color(0xFFDCDCDC),
    inverseSurface = Color(0xFFF5F5F5),
    inverseOnSurface = Color(0x66404040),
    onBackground = Color(0xFF0C0C0C),
    onTertiaryContainer = Color(0xFFF0F0F0),
    primary = Color(0xFF2395AF),
    onPrimary = Color(0xFFE4EAEB),
    secondary = Color(0xFFEEEFF1),
    onSecondary = Color(0xFF717171),
    tertiary = Color(0xFFBB86FC),
    errorContainer = Color(0xFFF0F0F0),
    onErrorContainer = Color(0xFF0C0C0C)
)

@Composable
fun JetDriveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}