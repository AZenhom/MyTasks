package com.ahmedzenhom.mytasks.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ahmedzenhom.mytasks.data.model.AccountModel
import com.ahmedzenhom.mytasks.data.model.FirebaseUserModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = "accountDataStore")

class AccountDataStore @Inject constructor(@ApplicationContext val context: Context) {

    private val accountModelPref = stringPreferencesKey("accountModel")
    private val firebaseUserPref = stringPreferencesKey("firebaseUser")
    private val accessTokenPref = stringPreferencesKey("accessToken")
    private val accessTokenLastRefreshPref = longPreferencesKey("accessTokenLastRefresh")

    suspend fun setAccountModel(model: AccountModel) {
        context.accountDataStore.edit {
            val json: String = Gson().toJson(model, AccountModel::class.java)
            it[accountModelPref] = json
        }
    }

    suspend fun getAccountModel(): AccountModel? {
        return context.accountDataStore.data.map {
            val json = it[accountModelPref]
            return@map if (json == null)
                null
            else
                Gson().fromJson(json, AccountModel::class.java)
        }.first()
    }

    suspend fun setFirebaseUser(user: FirebaseUserModel) {
        context.accountDataStore.edit {
            val json: String = Gson().toJson(user, FirebaseUserModel::class.java)
            it[firebaseUserPref] = json
        }
    }

    suspend fun getFirebaseUser(): FirebaseUserModel? {
        return context.accountDataStore.data.map {
            val json = it[firebaseUserPref]
            return@map if (json == null)
                null
            else
                Gson().fromJson(json, FirebaseUserModel::class.java)
        }.first()
    }

    suspend fun setAccessToken(value: String) {
        context.accountDataStore.edit {
            it[accessTokenPref] = value
        }
    }

    fun getAccessToken(): Flow<String?> {
        return context.accountDataStore.data.map {
            it[accessTokenPref]
        }
    }

    suspend fun setAccessTokenLastRefresh(value: Long) {
        context.accountDataStore.edit {
            it[accessTokenLastRefreshPref] = value
        }
    }

    suspend fun getAccessTokenLastRefresh(): Long {
        return context.accountDataStore.data.map {
            it[accessTokenLastRefreshPref]
        }.first() ?: 0L
    }

    suspend fun clear() {
        context.accountDataStore.edit { it.clear() }
    }
}