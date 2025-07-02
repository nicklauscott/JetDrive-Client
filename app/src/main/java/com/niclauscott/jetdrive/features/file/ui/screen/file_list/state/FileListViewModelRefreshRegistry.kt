package com.niclauscott.jetdrive.features.file.ui.screen.file_list.state

object FileListViewModelRefreshRegistry {
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