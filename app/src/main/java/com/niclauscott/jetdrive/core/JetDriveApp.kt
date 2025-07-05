package com.niclauscott.jetdrive.core

import android.app.Application
import com.niclauscott.jetdrive.core.cache.di.inMemoryCacheModule
import com.niclauscott.jetdrive.core.database.di.databaseModule
import com.niclauscott.jetdrive.core.datastore.di.dataStoreModule
import com.niclauscott.jetdrive.core.http_client.di.httpClientModule
import com.niclauscott.jetdrive.features.auth.di.authModule
import com.niclauscott.jetdrive.core.splash.di.splashModule
import com.niclauscott.jetdrive.features.auth.google.di.googleAuthModule
import com.niclauscott.jetdrive.features.file.di.fileModule
import com.niclauscott.jetdrive.features.landing.di.landingModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
class JetDriveApp: Application(), KoinStartup {
    @KoinExperimentalAPI
    override fun onKoinStartup() = KoinConfiguration {
        androidLogger()
        androidContext(this@JetDriveApp)
        modules(
            dataStoreModule, httpClientModule, databaseModule,
            inMemoryCacheModule, splashModule, authModule,
            googleAuthModule, landingModule, fileModule,
        )
    }
}