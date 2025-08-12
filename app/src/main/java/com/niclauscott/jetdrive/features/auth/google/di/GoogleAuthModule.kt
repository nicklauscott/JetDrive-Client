package com.niclauscott.jetdrive.features.auth.google.di

import com.niclauscott.jetdrive.BuildConfig
import com.niclauscott.jetdrive.features.auth.domain.repository.OAuthClient
import com.niclauscott.jetdrive.features.auth.google.GoogleAuth
import org.koin.core.qualifier.named
import org.koin.dsl.module

val googleAuthModule = module {
    single(named("google_client_id")) { BuildConfig.GOOGLE_CLIENT_ID }
    factory<OAuthClient> { GoogleAuth(get(), get(), get(named("google_client_id")), get()) }
}