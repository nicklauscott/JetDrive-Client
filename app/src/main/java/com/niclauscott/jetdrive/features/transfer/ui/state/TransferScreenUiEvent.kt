package com.niclauscott.jetdrive.features.transfer.ui.state

import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
import java.util.UUID

sealed interface TransferScreenUiEvent {
    data class Move(val from: Int, val to: Int, val type: TransferType): TransferScreenUiEvent
    data class ToggleTransfer(val id: UUID, val type: TransferType): TransferScreenUiEvent
    data object GoBack: TransferScreenUiEvent
    data object ToggleAllTransfer: TransferScreenUiEvent
    data object CancelAllTransfer: TransferScreenUiEvent
    data class ToggleSpecificTransfers(val type: TransferType): TransferScreenUiEvent
    data class CancelSpecificTransfers(val type: TransferType): TransferScreenUiEvent
}