package com.niclauscott.jetdrive.core.database.di

import androidx.room.Room
import com.niclauscott.jetdrive.core.database.JetDriveDatabase
import com.niclauscott.jetdrive.core.database.dao.TransferDao
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            context = get(),
            JetDriveDatabase::class.java,
            "jet_drive.db"
        )
        .fallbackToDestructiveMigration(true) // replace with 'false' in production
        .build()
    }

    single<TransferDao> {
        get<JetDriveDatabase>().transferDao()
    }

}