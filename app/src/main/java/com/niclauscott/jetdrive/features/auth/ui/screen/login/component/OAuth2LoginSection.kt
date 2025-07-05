package com.niclauscott.jetdrive.features.auth.ui.screen.login.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.niclauscott.jetdrive.R

@Composable
fun OAuth2LoginSection(modifier: Modifier = Modifier, onClicked: () -> Unit) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Sign in with",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            OAuthClient(
                modifier = Modifier,
                painter = painterResource(R.drawable.google_icon),
                contentDescription = stringResource(R.string.google_login),
                onClicked = onClicked
            )
        }
    }
}

@Composable
fun OAuthClient(
    modifier: Modifier = Modifier, painter: Painter,
    contentDescription: String, onClicked: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f, label = "ScaleAnimation"
    )

    Image(
        modifier = modifier.size(35.dp)
            .clip(CircleShape)
            .clickable(
                onClick = onClicked,
                interactionSource = interactionSource,
                indication = null
            )
            .scale(scale),
        painter = painter,
        contentDescription = contentDescription
    )
}
