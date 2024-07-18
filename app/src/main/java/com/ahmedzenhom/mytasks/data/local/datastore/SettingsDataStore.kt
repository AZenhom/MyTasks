package com.ahmedzenhom.mytasks.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settingsDataStore")

class SettingsDataStore @Inject constructor(@ApplicationContext val context: Context) {

    private val languagePref = stringPreferencesKey("language")
    private val lastTasksLocalUpdatePref = longPreferencesKey("lastTasksLocalUpdate")

    suspend fun setLanguage(value: String) {
        context.settingsDataStore.edit {
            it[languagePref] = value
        }
    }

    suspend fun getLanguage(): String {
        return context.settingsDataStore.data.map {
            it[languagePref] ?: "en"
        }.first()
    }

    suspend fun setLastTasksLocalUpdate(value: Long) {
        context.settingsDataStore.edit {
            it[lastTasksLocalUpdatePref] = value
        }
    }

    suspend fun getLastTasksLocalUpdate(): Long {
        return context.settingsDataStore.data.map {
            it[lastTasksLocalUpdatePref] ?: 0
        }.first()
    }
}