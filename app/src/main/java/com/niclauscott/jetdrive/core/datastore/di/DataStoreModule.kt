package com.niclauscott.jetdrive.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.niclauscott.jetdrive.core.datastore.UserPreferences
import com.niclauscott.jetdrive.core.datastore.UserPreferencesSerializer
import org.koin.dsl.module

val Context.dataStore by dataStore(fileName = "user-preferences", serializer = UserPreferencesSerializer)

val dataStoreModule = module {

    single<DataStore<UserPreferences>> {
        val context: Context = get()
        context.dataStore
    }

}