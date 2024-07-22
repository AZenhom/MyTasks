package com.ahmedzenhom.mytasks.data.repository

import com.ahmedzenhom.mytasks.data.local.datastore.AccountDataStore
import com.ahmedzenhom.mytasks.data.local.datastore.SettingsDataStore
import com.ahmedzenhom.mytasks.data.local.db.dao.TasksDao
import com.ahmedzenhom.mytasks.data.model.AccountModel
import com.ahmedzenhom.mytasks.data.model.FirebaseUserModel
import com.ahmedzenhom.mytasks.data.remote.auth.AuthApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val accountDataStore: AccountDataStore,
    private val settingsDataStore: SettingsDataStore,
    private val taskDao: TasksDao,
    private val authApiService: AuthApiService,
) : BaseRepository() {

    suspend fun logout() = execute {
        FirebaseAuth.getInstance().signOut()
        accountDataStore.clear()
        taskDao.deleteAllTask()
        settingsDataStore.setLastTasksLocalUpdate(0)
    }

    // Remote

    suspend fun getUserByPhone(phone: String) = execute {
        return@execute authApiService.getUserIfExists(phone).data
    }

    suspend fun registerUser(name: String): Boolean = execute {
        val firebaseUser = accountDataStore.getFirebaseUser() ?: return@execute false
        val accountModel = AccountModel(
            id = firebaseUser.uid,
            phone = firebaseUser.phone,
            name = name
        )
        authApiService.registerUser(accountModel)
        saveAccountModel(accountModel)
        return@execute true
    }

    // Datastore

    suspend fun getAccount() = execute { return@execute accountDataStore.getAccountModel() }

    suspend fun saveFirebaseUser(firebaseUser: FirebaseUser) = execute {
        accountDataStore
            .setFirebaseUser(FirebaseUserModel.fromFirebaseUser(firebaseUser))
    }

    suspend fun saveAccountModel(model: AccountModel) = execute {
        accountDataStore.setAccountModel(model)
    }

}