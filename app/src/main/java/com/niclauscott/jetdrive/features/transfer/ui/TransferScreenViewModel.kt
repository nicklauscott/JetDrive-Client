package com.niclauscott.jetdrive.features.transfer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
import com.niclauscott.jetdrive.core.database.domain.model.Download
import com.niclauscott.jetdrive.core.database.domain.model.Upload
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import com.niclauscott.jetdrive.features.transfer.domain.repository.TransferRepository
import com.niclauscott.jetdrive.features.transfer.ui.state.TransferScreenUiEvent
import com.niclauscott.jetdrive.features.transfer.ui.state.TransferScreenUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransferScreenViewModel(
    private val backStack: NavBackStack,
    private val landingScreenViewModel: LandingScreenViewModel,
    private val repository: TransferRepository
): ViewModel() {

    val state: StateFlow<TransferScreenUiState> = repository
        .getAllIncompleteTransfer()
        .map { transfers ->
            TransferScreenUiState(
                isDownloadTasksLoading = false,
                downloadTasks = transfers.filterIsInstance<Download>(),
                isUploadTasksLoading = false,
                uploadTasks = transfers.filterIsInstance<Upload>()
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TransferScreenUiState(isDownloadTasksLoading = true, isUploadTasksLoading = true)
        )

    init { landingScreenViewModel.showBottomBars() }

    fun onEvent(event: TransferScreenUiEvent) {
        when (event) {
            is TransferScreenUiEvent.Move -> {
                if (event.type == TransferType.DOWNLOAD) updateDownloadQueuePositions(event.from, event.to)
                else updateUploadQueuePositions(event.from, event.to)
            }
            is TransferScreenUiEvent.ToggleTransfer -> {
                viewModelScope.launch(Dispatchers.IO) { repository.toggleTransfer(event.id, event.type) }
            }
            TransferScreenUiEvent.GoBack -> {
                landingScreenViewModel.hideBottomBars()
                backStack.removeAt(backStack.lastIndex)
            }
            TransferScreenUiEvent.CancelAllTransfer -> {
                viewModelScope.launch { repository.cancelAllTransfer() }
            }
            is TransferScreenUiEvent.CancelSpecificTransfers -> {
                viewModelScope.launch {
                    repository.cancelAllSpecificTransfer(event.type)
                }
            }
            TransferScreenUiEvent.ToggleAllTransfer -> {
                viewModelScope.launch {
                    if (state.value.isAllTransferPaused) repository.startAllTransfer()
                    else repository.pauseAllTransfer()
                }
            }
            is TransferScreenUiEvent.ToggleSpecificTransfers -> toggleSpecificTransfers(event.type)
        }
    }

    private fun toggleSpecificTransfers(type: TransferType) {
        if (type == TransferType.DOWNLOAD) {
            viewModelScope.launch {
                if (state.value.isDownloadsPaused) repository.startAllSpecificTransfer(type)
                else repository.pauseAllSpecificTransfer(type)
            }
            return
        }
        viewModelScope.launch {
            if (state.value.isUploadsPaused) repository.startAllSpecificTransfer(type)
            else repository.pauseAllSpecificTransfer(type)
        }
    }

    private fun updateDownloadQueuePositions(from: Int, to: Int) {
        val currentItems = state.value.downloadTasks.toMutableList()
        val movedItem = currentItems.removeAt(from)
        currentItems.add(to, movedItem)
        val updatedItems = currentItems.mapIndexed { index, item -> item.copy(queuePosition = index) }
        viewModelScope.launch { repository.updateDownloadStatus(updatedItems) }
    }

    private fun updateUploadQueuePositions(from: Int, to: Int) {
        val currentItems = state.value.uploadTasks.toMutableList()
        val movedItem = currentItems.removeAt(from)
        currentItems.add(to, movedItem)
        val updatedItems = currentItems.mapIndexed { index, item -> item.copy(queuePosition = index) }
        viewModelScope.launch { repository.updateUploadStatus(updatedItems) }
    }

}