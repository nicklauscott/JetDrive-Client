package com.niclauscott.jetdrive.core.ui.util

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Int.percentOfScreenWidth(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    return with(density) {
        (this@percentOfScreenWidth * configuration.screenWidthDp / 100).dp
    }
}

@Composable
fun Double.percentOfScreenWidth(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    return with(density) {
        (this@percentOfScreenWidth * configuration.screenWidthDp / 100.0).dp
    }
}

@Composable
fun Float.percentOfScreenWidth(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    return with(density) {
        (this@percentOfScreenWidth * configuration.screenWidthDp / 100f).dp
    }
}

@Composable
fun Int.percentOfScreenHeight(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    return with(density) {
        (this@percentOfScreenHeight * configuration.screenHeightDp / 100).dp
    }
}

@Composable
fun Double.percentOfScreenHeight(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    return with(density) {
        (this@percentOfScreenHeight * configuration.screenHeightDp / 100.0).dp
    }
}

@Composable
fun Float.percentOfScreenHeight(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    return with(density) {
        (this@percentOfScreenHeight * configuration.screenHeightDp / 100f).dp
    }
}

@Composable
fun cornerRadius(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    return with(density) {
        (kotlin.math.min(
            configuration.screenWidthDp,
            configuration.screenHeightDp
        ) * 0.03).dp
    }
}

@Composable
fun BoxWithConstraintsScope.percentOfMaxWidth(amount: Int): Dp {
    return (amount * maxWidth.value / 100).dp
}

@Composable
fun BoxWithConstraintsScope.percentOfMaxWidth(amount: Double): Dp {
    return (amount * maxWidth.value / 100.0).dp
}

@Composable
fun BoxWithConstraintsScope.percentOfMaxHeight(amount: Int): Dp {
    return (amount * maxHeight.value / 100).dp
}

@Composable
fun BoxWithConstraintsScope.percentOfMaxHeight(amount: Double): Dp {
    return (amount * maxHeight.value / 100.0).dp
}