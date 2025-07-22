package com.niclauscott.jetdrive.core

import android.app.Application
import androidx.work.Configuration
import com.niclauscott.jetdrive.core.cache.di.inMemoryCacheModule
import com.niclauscott.jetdrive.core.database.di.databaseModule
import com.niclauscott.jetdrive.core.datastore.di.dataStoreModule
import com.niclauscott.jetdrive.core.http_client.di.httpClientModule
import com.niclauscott.jetdrive.features.auth.di.authModule
import com.niclauscott.jetdrive.core.splash.di.splashModule
import com.niclauscott.jetdrive.core.sync.di.syncModule
import com.niclauscott.jetdrive.core.transfer.di.appTransferModule
import com.niclauscott.jetdrive.features.auth.google.di.googleAuthModule
import com.niclauscott.jetdrive.features.file.di.fileModule
import com.niclauscott.jetdrive.features.home.di.homeModule
import com.niclauscott.jetdrive.features.landing.di.landingModule
import com.niclauscott.jetdrive.features.profile.di.profileModule
import com.niclauscott.jetdrive.features.transfer.di.transferModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
class JetDriveApp: Application(), KoinStartup, Configuration.Provider {

    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()
    }

    @KoinExperimentalAPI
    override fun onKoinStartup() = KoinConfiguration {
        androidLogger()
        androidContext(this@JetDriveApp)
        modules(
            dataStoreModule, httpClientModule, databaseModule,
            inMemoryCacheModule, splashModule, authModule,
            googleAuthModule, landingModule, fileModule,
            homeModule, profileModule, appTransferModule,
            transferModule, syncModule
        )
    }
}