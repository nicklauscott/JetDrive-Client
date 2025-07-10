package com.niclauscott.jetdrive.features.file.ui.screen.file_preview.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.niclauscott.jetdrive.core.ui.util.DeviceConfiguration
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.features.file.domain.model.AudioMetadata
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.util.formatTime
import kotlinx.coroutines.delay

@Composable
fun AudioPlayer(
    modifier: Modifier = Modifier,
    deviceConfiguration: DeviceConfiguration,
    player: ExoPlayer?,
    fileNode: FileNode,
    audioMetadata: AudioMetadata?,
    onDispose: () -> Unit,
) {
    var position by remember { mutableLongStateOf(player?.currentPosition ?: 0L) }
    var duration by remember { mutableLongStateOf(player?.duration?.takeIf { it > 0 } ?: 1L) }
    var isPlaying by remember { mutableStateOf(player?.isPlaying ?: false) }

    LaunchedEffect(player) {
        while (true) {
            if (player != null) {
                position = player.currentPosition
                duration = player.duration.takeIf { it > 0 } ?: 1L
                isPlaying = player.isPlaying
            }
            delay(500)
        }
    }

    DisposableEffect(Unit) { onDispose { onDispose() } }

    when {
        player == null -> {}
        else -> {
            AndroidView(
                modifier = Modifier.size(5.dp),
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = player
                    }
                },
                update = { playerView ->
                    playerView.player = player
                }
            )

            position = player.currentPosition
            duration = player.duration.takeIf { it > 0 } ?: 1L
            isPlaying = player.isPlaying

            when (deviceConfiguration) {
                DeviceConfiguration.MOBILE_PORTRAIT -> {
                    PortraitMusicPlayer(
                        modifier = modifier,
                        player = player,
                        fileNode = fileNode,
                        audioMetadata = audioMetadata,
                        position = position,
                        duration = duration,
                        isPlaying = isPlaying,
                        onPositionChange = { position = it },
                    ) { player.seekTo(position) }
                }
                else -> {
                    LandscapeMusicPlayer(
                        modifier = modifier,
                        player = player,
                        fileNode = fileNode,
                        audioMetadata = audioMetadata,
                        position = position,
                        duration = duration,
                        isPlaying = isPlaying,
                        onPositionChange = { position = it },
                    ) { player.seekTo(position) }
                }
            }
        }
    }
}

@Composable
fun LandscapeMusicPlayer(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    fileNode: FileNode,
    audioMetadata: AudioMetadata?,
    position: Long, duration: Long, isPlaying: Boolean,
    onPositionChange: (Long) -> Unit,
    onSeekFinished: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 2.percentOfScreenHeight())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (audioMetadata == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                )
            } else {
                audioMetadata.base64CoverArt?.let {
                    Base64Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        base64String = audioMetadata.base64CoverArt
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .padding(vertical = 5.percentOfScreenHeight()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (audioMetadata == null) {
                    Text(
                        text = fileNode.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.MiddleEllipsis
                    )
                } else {
                    audioMetadata.title?.let {
                        Text(
                            text = audioMetadata.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            overflow = TextOverflow.MiddleEllipsis
                        )
                    }

                    audioMetadata.artist?.let {
                        Text(
                            text = audioMetadata.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            overflow = TextOverflow.MiddleEllipsis
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    MusicSlider(
                        position = position,
                        duration = duration,
                        onPositionChange = onPositionChange,
                        onSeekFinished = onSeekFinished
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = formatTime(position), color = Color.White)
                        Text(text = formatTime(duration), color = Color.White)
                    }
                }


                Row(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModernPlayPauseButton(
                        isPlaying = isPlaying,
                        onToggle = {
                            if (player.isPlaying) player.pause() else player.play()
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun PortraitMusicPlayer(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    fileNode: FileNode,
    audioMetadata: AudioMetadata?,
    position: Long, duration: Long, isPlaying: Boolean,
    onPositionChange: (Long) -> Unit,
    onSeekFinished: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (audioMetadata == null) {
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                )

                Box(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = fileNode.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.MiddleEllipsis
                    )
                }
            } else {
                audioMetadata.base64CoverArt?.let {
                    Base64Image(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        base64String = audioMetadata.base64CoverArt
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxWidth()
                        .padding(vertical = 1.percentOfScreenHeight()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    audioMetadata.title?.let {
                        Text(
                            text = audioMetadata.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            overflow = TextOverflow.MiddleEllipsis
                        )
                    }

                    audioMetadata.artist?.let {
                        Text(
                            text = audioMetadata.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            overflow = TextOverflow.MiddleEllipsis
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // progress bar
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MusicSlider(
                    position = position,
                    duration = duration,
                    onPositionChange = onPositionChange,
                    onSeekFinished = onSeekFinished
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = formatTime(position), color = Color.White)
                    Text(text = formatTime(duration), color = Color.White)
                }
            }


            Row(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                ModernPlayPauseButton(
                    isPlaying = isPlaying,
                    onToggle = {
                        if (player.isPlaying) player.pause() else player.play()
                    }
                )
            }
        }
    }
}