package com.niclauscott.jetdrive.features.file.ui.screen.copy_move

object CopyMoveViewModelRefreshRegistry {
    private val refreshMap = mutableListOf<String>()

    fun markForRefresh(folderId: String) {
        refreshMap.add(folderId)
    }

    fun shouldRefresh(folderId: String): Boolean {
        return refreshMap.contains(folderId)
    }

    fun clear(folderId: String) {
        refreshMap.remove(folderId)
    }
}