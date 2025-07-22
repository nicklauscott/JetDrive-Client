package com.niclauscott.jetdrive.core.sync.di

import com.niclauscott.jetdrive.core.sync.data.repository.FileSyncRepository
import com.niclauscott.jetdrive.core.sync.domain.service.FileSyncService
import com.niclauscott.jetdrive.core.sync.domain.service.InMemoryCache
import org.koin.dsl.module

val syncModule = module {

    single { InMemoryCache() }
    single { FileSyncRepository(get(), get(), get()) }
    single { FileSyncService(get(), get(), get()) }

}