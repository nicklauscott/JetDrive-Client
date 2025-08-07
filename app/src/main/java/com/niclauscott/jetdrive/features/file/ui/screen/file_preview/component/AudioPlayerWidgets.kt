package com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.niclauscott.jetdrive.features.file.domain.util.formatTime


@Composable
fun Base64Image(base64String: String, modifier: Modifier = Modifier) {
    val imageBitmap = remember(base64String) {
        try {
            val base64Data = base64String.substringAfter("base64,")
            val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Cover Art",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        // Placeholder if image decoding failed
        Box(
            modifier = modifier.background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text("No Image", color = Color.White)
        }
    }
}

@Composable
fun getAverageColor(imageBitmap: ImageBitmap): Color {
    var averageColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(Unit) {

        val compatibleBitmap = imageBitmap.asAndroidBitmap()
            .copy(Bitmap.Config.ARGB_8888, false)

        val pixels = IntArray(compatibleBitmap.width * compatibleBitmap.height)
        compatibleBitmap.getPixels(
            pixels, 0, compatibleBitmap.width, 0, 0,
            compatibleBitmap.width, compatibleBitmap.height
        )

        var redSum = 0
        var greenSum = 0
        var blueSum = 0

        for (pixel in pixels) {
            val red = android.graphics.Color.red(pixel)
            val green = android.graphics.Color.green(pixel)
            val blue = android.graphics.Color.blue(pixel)

            redSum += red
            greenSum += green
            blueSum += blue
        }

        val pixelCount = pixels.size
        val averageRed = redSum / pixelCount
        val averageGreen = greenSum / pixelCount
        val averageBlue = blueSum / pixelCount
        averageColor = Color(averageRed, averageGreen, averageBlue)
    }

    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(averageColor.toArgb(), hsl)
    val darkerLightness = hsl[2] - 0.1f

    val darkerColor = ColorUtils.HSLToColor(
        floatArrayOf(
            hsl[0],
            hsl[1], darkerLightness
        )
    )
    return Color(darkerColor)
}

@Composable
fun ModernPlayPauseButton(
    isPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 65.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedSize by animateDpAsState(
        targetValue = if (isPressed) size * 0.9f else size,
        animationSpec = tween(100, easing = EaseInOutCubic),
        label = "button_size"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = tween(100),
        label = "elevation"
    )

    Surface(
        modifier = modifier
            .size(animatedSize)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onToggle() }
            .padding(4.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = animatedElevation,
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isPlaying,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) + scaleIn(
                        animationSpec = tween(200),
                        initialScale = 0.8f
                    ) togetherWith fadeOut(animationSpec = tween(200)) + scaleOut(
                        animationSpec = tween(200),
                        targetScale = 0.8f
                    )
                },
                label = "play_pause_animation"
            ) { playing ->
                Icon(
                    imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(size * 0.4f)
                        .offset(x = if (!playing) 2.dp else 0.dp) // Slight offset for play icon centering
                )
            }
        }
    }
}

@Composable
fun NeonPlayPauseButton(
    isPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 65.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val neonColor = Color(0xFF00FFFF) // Cyan neon
    val pulseAnimation by rememberInfiniteTransition().animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_pulse"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "press_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(pressScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        // Outer neon glow
        Box(
            modifier = Modifier
                .size(size * pulseAnimation)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            neonColor.copy(alpha = 0.4f),
                            neonColor.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(12.dp)
        )

        // Inner glow
        Box(
            modifier = Modifier
                .size(size * 0.8f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            neonColor.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(6.dp)
        )

        // Main button
        Surface(
            modifier = Modifier
                .size(size - 8.dp)
                .padding(2.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.8f),
            border = BorderStroke(2.dp, neonColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                neonColor.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isPlaying,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) +
                                scaleIn(animationSpec = tween(300, easing = EaseOutBack))) togetherWith
                                (fadeOut(animationSpec = tween(150)) +
                                        scaleOut(animationSpec = tween(150)))
                    },
                    label = "neon_animation"
                ) { playing ->
                    Icon(
                        imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playing) "Pause" else "Play",
                        tint = neonColor,
                        modifier = Modifier
                            .size(size * 0.4f)
                            .offset(x = if (!playing) 1.dp else 0.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSlider(
    position: Long,
    duration: Long,
    onPositionChange: (Long) -> Unit,
    onSeekFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSliding by remember { mutableStateOf(false) }

    val safeDuration = if (duration > 0) duration else 1L
    val safePosition = position.coerceIn(0L, safeDuration)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Time display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(safePosition),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Text(
                text = formatTime(safeDuration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Modern Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background glow effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(3.dp)
                    )
                    .blur(4.dp)
            )

            // Main slider
            Slider(
                value = safePosition.toFloat(),
                onValueChange = {
                    onPositionChange(it.toLong())
                    isSliding = true
                },
                onValueChangeFinished = {
                    onSeekFinished()
                    isSliding = false
                },
                valueRange = 0f..safeDuration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                track = { sliderState ->
                    // Custom track with gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(2.dp)
                            )
                    ) {
                        // Progress fill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    fraction = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                                )
                                .height(4.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.onBackground,
                                            MaterialTheme.colorScheme.tertiary,
                                            MaterialTheme.colorScheme.primary
                                        )
                                    ),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                },
                thumb = {
                    // Custom thumb with glow effect
                    Box(
                        modifier = Modifier
                            .size(if (isSliding) 24.dp else 20.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    radius = 40f
                                ),
                                shape = CircleShape
                            )
                            .animateContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    Color.White,
                                    CircleShape
                                )
                                .shadow(
                                    elevation = if (isSliding) 8.dp else 4.dp,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            )
        }
    }
}