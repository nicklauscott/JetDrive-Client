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
    primary = Color(0xFF1DB5E0),
    onPrimary = Color(0xFF002E3A),
    secondary = Color(0xFF2B2D32),
    onSecondary = Color(0xFFB0B3B8),
    tertiary = Color(0xFFAE7CFF),
    errorContainer = Color(0xFF3A1E1E),
    onErrorContainer = Color(0xFFFFBDBD)
)

/*
val LightColorScheme = lightColorScheme(
    background = Color(0xFFF2F4F7),
    inverseSurface = Color(0xFFE4E6EB),
    inverseOnSurface = Color(0xFF4C4F54),
    onBackground = Color(0xFF121212),
    primary = Color(0xFF009ECF),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFE9EBED),
    onSecondary = Color(0xFF555B61),
    tertiary = Color(0xFF7D4CFF),
    errorContainer = Color(0xFFFFE8E8),
    onErrorContainer = Color(0xFF4C1C1C)
)

 */

/* Original
private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF070707),
    inverseSurface = Color(0xFF151515),
    inverseOnSurface = Color(0x66494949),
    onBackground = Color(0xFFF6F6F7),
    primary = Color(0xFF2395AF),
    onPrimary = Color(0xFFE4EAEB),
    secondary = Color(0xFF212329),
    onSecondary = Color(0xFF666769),
    tertiary = Color(0xFFBB86FC),
    errorContainer = Color(0xFF424447),
    onErrorContainer = Color(0xFFF6F6F7)
)
*/

val LightColorScheme = lightColorScheme(
    background = Color(0xFFDCDCDC),
    inverseSurface = Color(0xFFF5F5F5), // 0xFFF5F5F5 0xFF666666
    inverseOnSurface = Color(0x66404040), // 0x66404040 0xFFBEC7D5
    onBackground = Color(0xFF0C0C0C),
    primary = Color(0xFF2395AF),
    onPrimary = Color(0xFFE4EAEB),
    secondary = Color(0xFFEEEFF1),
    onSecondary = Color(0xFF717171),
    tertiary = Color(0xFFBB86FC),
    errorContainer = Color(0xFFF0F0F0),
    onErrorContainer = Color(0xFF0C0C0C)
)

//*/

@Composable
fun JetDriveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    /*
    val view = LocalView.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.onSurface.toArgb()
        if (!darkTheme) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
        when (deviceConfiguration) {
            DeviceConfiguration.MOBILE_PORTRAIT -> window.navigationBarColor = colorScheme.onSurface.toArgb()
            else -> window.navigationBarColor = colorScheme.onSurface.toArgb()
        }
    }

     */

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}