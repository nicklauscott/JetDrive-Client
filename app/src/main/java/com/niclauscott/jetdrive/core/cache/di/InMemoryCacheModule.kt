package com.niclauscott.jetdrive.core.cache.di

import com.niclauscott.jetdrive.core.cache.InMemoryCache
import com.niclauscott.jetdrive.features.file.domain.model.FileNodeTree
import org.koin.dsl.module

val inMemoryCacheModule = module {
    single { InMemoryCache<String, FileNodeTree>(500) }
}