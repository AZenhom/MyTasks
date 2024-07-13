package com.ahmedzenhom.mytasks.data.repository

import com.ahmedzenhom.mytasks.data.local.datastore.AccountDataStore
import com.ahmedzenhom.mytasks.data.local.datastore.SettingsDataStore
import com.ahmedzenhom.mytasks.data.model.AccountModel
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val accountDataStore: AccountDataStore,
    private val settingsDataStore: SettingsDataStore,
) : BaseRepository() {

    suspend fun isLocaleArabic(): Boolean = execute {
        return@execute settingsDataStore.getLanguage().contains("ar")
    }

    suspend fun getCurrentLanguage(): String = execute {
        val currentLanguage = settingsDataStore.getLanguage()
        setCurrentLanguage(currentLanguage) // Just in case it's set not just a ternary return
        return@execute currentLanguage
    }

    suspend fun setCurrentLanguage(value: String) = execute {
        settingsDataStore.setLanguage(value)
    }

    suspend fun isLoggedIn(): Boolean = execute {
        return@execute accountDataStore.getAccountModel() != null
    }

    suspend fun getCurrentUser(): AccountModel? = execute {
        return@execute accountDataStore.getAccountModel()
    }

    suspend fun getCurrentUserId(): String? = execute {
        return@execute accountDataStore.getAccountModel()?.id
    }
}