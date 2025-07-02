package com.niclauscott.jetdrive.features.auth.ui.screen.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
    painter: Painter, contentDescription: String,
    onClicked: () -> Unit) {
    IconButton(onClick = onClicked, modifier = Modifier.padding(3.dp)) {
        Image(
            modifier = modifier.size(35.dp),
            painter = painter,
            contentDescription = contentDescription
        )
    }
}
