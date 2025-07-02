package com.niclauscott.jetdrive.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.niclauscott.jetdrive.R

// Set of Material typography styles to start with
val Typography: Typography @Composable get() = Typography(
    titleLarge = TextStyle(
        fontFamily = customFontFamily[1],
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    titleSmall = TextStyle(
        fontFamily = customFontFamily[2],
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = customFontFamily[3],
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = customFontFamily[4],
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = customFontFamily[5],
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = customFontFamily[5],
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)


val customFontFamily: List<FontFamily>
    get() = listOf(
        FontFamily(Font(R.font.nationale_black, FontWeight.Black)),
        FontFamily(Font(R.font.nationale_bold, FontWeight.Bold)),
        FontFamily(Font(R.font.nationale_demi_bold, FontWeight.SemiBold)),
        FontFamily(Font(R.font.nationale_medium, FontWeight.Medium)),
        FontFamily(Font(R.font.nationale_regular, FontWeight.Normal)),
        FontFamily(Font(R.font.nationale_italic, FontWeight.Thin)),
        FontFamily(Font(R.font.nationale_light, FontWeight.Light))
    )
