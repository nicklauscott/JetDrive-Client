package com.niclauscott.jetdrive.features.file.ui.screen.file_preview

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FilePreviewRepository
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.state.FilePreviewState
import com.niclauscott.jetdrive.features.file.ui.screen.file_preview.state.FilePreviewUiEvent
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.media3.common.PlaybackException

class FilePreviewScreenViewModel(
    private val fileNode: FileNode,
    private val backStack: NavBackStack,
    private val landingScreenViewModel: LandingScreenViewModel,
    private val repository: FilePreviewRepository
): ViewModel() {

    private val _state: MutableState<FilePreviewState> = mutableStateOf(FilePreviewState(fileNode = fileNode))
    val state: State<FilePreviewState> = _state

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState: StateFlow<ExoPlayer?> = _playerState

    private var currentPosition: Long = 0L

    init {
        landingScreenViewModel.hideBottomBars()
        _state.value = state.value.copy(isFileUrlLoading = true)
        viewModelScope.launch {
            Log.d(TAG("FilePreviewScreenViewModel"), "init init: ")
            val response = repository.getFileUri(fileNode.id)

            if (response is FileResponse.Failure) {
                Log.d(TAG("FilePreviewScreenViewModel"), "init failure: $response")
                _state.value = state.value.copy(isFileUrlLoading = false, error = response.message)
                return@launch
            }

            if (response is FileResponse.Successful) {
                Log.d(TAG("FilePreviewScreenViewModel"), "init failure: $response")
                _state.value = state.value.copy(isFileUrlLoading = false, fileUrl = response.data)
            }

        }
    }

    fun onEvent(event: FilePreviewUiEvent) {
        when (event) {
            FilePreviewUiEvent.GoBack -> {
                landingScreenViewModel.showBottomBars()
                backStack.removeAt(backStack.lastIndex)
            }

            is FilePreviewUiEvent.InitializePlayer -> {
                state.value.fileUrl?.let {
                    initializePlayer(event.context, it)
                }
            }

            FilePreviewUiEvent.Release -> {
                landingScreenViewModel.showBottomBars()
                savePlayerState()
                onCleared()
            }

            FilePreviewUiEvent.ConfigurationChanged -> {
                landingScreenViewModel.hideBottomBars()
            }
        }
    }

    private fun initializePlayer(context: Context, videoUrl: String) {
        if (_playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = ExoPlayer.Builder(context).build().also {
                    val mediaItem = MediaItem.fromUri(videoUrl.toUri())
                    it.setMediaItem(mediaItem)
                    it.prepare()
                    it.playWhenReady = true
                    it.seekTo(currentPosition)
                    it.addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            _state.value = state.value.copy(error = handleError(error))
                        }
                    })
                }
                _playerState.value = exoPlayer
                if (state.value.fileType == "audio") {
                    val response = repository.getAudioMetadata(fileNode.id)
                    if (response is FileResponse.Successful) {
                        _state.value = state.value.copy(audioMetadata = response.data)
                    }
                }
            }
        }
    }

    private fun savePlayerState() {
        _playerState.value?.let {
            currentPosition = it.currentPosition
        }
    }

    private fun handleError(error: PlaybackException): String {
        return when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                "Network connection error"
            }
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
                "File not found"
            }
            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> {
                "Decoder initialization error"
            }
            else -> {
                "Other error: ${error.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _playerState.value?.release()
        _playerState.value = null
    }
}