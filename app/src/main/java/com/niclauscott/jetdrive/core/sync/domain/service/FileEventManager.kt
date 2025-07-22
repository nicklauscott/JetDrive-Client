package com.niclauscott.jetdrive.core.sync.domain.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object FileEventManager {
    private val _event = MutableSharedFlow<FileSystemEvent>(replay = 1)
    val event = _event.asSharedFlow()

    fun emit(event: FileSystemEvent) {
        _event.tryEmit(event)
    }
}