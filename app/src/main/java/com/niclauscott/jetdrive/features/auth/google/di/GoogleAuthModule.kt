package com.niclauscott.jetdrive.features.auth.google.di

import com.niclauscott.jetdrive.features.auth.domain.repository.OAuthClient
import com.niclauscott.jetdrive.features.auth.google.GoogleAuth
import org.koin.dsl.module

val googleAuthModule = module {
    factory<OAuthClient> { GoogleAuth(get(), get(), get()) }
}