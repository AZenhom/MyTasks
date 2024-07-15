package com.ahmedzenhom.mytasks.data.repository

import com.ahmedzenhom.mytasks.data.local.datastore.AccountDataStore
import com.ahmedzenhom.mytasks.data.model.AccountModel
import com.ahmedzenhom.mytasks.data.model.FirebaseUserModel
import com.ahmedzenhom.mytasks.data.remote.auth.AuthApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val accountDataStore: AccountDataStore,
    private val authApiService: AuthApiService,
) : BaseRepository() {

    suspend fun logout() {
        accountDataStore.clear()
        FirebaseAuth.getInstance().signOut()
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

    suspend fun saveFirebaseUser(firebaseUser: FirebaseUser) {
        accountDataStore
            .setFirebaseUser(FirebaseUserModel.fromFirebaseUser(firebaseUser))
    }

    suspend fun saveAccountModel(model: AccountModel) {
        accountDataStore.setAccountModel(model)
    }

    suspend fun refreshAndSaveIdToken() {
        val token =
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token ?: return
        accountDataStore.setAccessToken(token)
    }


}